import torch
import torch.nn as nn
from torch.utils.data import DataLoader
import torchvision.transforms as transforms
import torchvision
import numpy as np
import torch.optim as optim
from torchvision.models import vgg19
import matplotlib.pyplot as plt

import SRGanNet as Sr
import Utils as Ut
import ImageLoader as Il
import matplotlib.pyplot as plt

def tensorToImage(image):
    image = image.detach().cpu()
    img_grid = torchvision.utils.make_grid(image)
    img_grid = np.transpose(img_grid, (1,2,0))
    plt.imshow(img_grid)
    plt.show()

def adv_loss(d_result):
    return (-1*torch.log10(d_result)).mean()

transform = transforms.ToTensor()

if __name__=='__main__':
    image_path = 'C:/Users/kjm04/Downloads/celeba-dataset/img_align_celeba/img_align_celeba'
    batch_size = 1
    device = torch.device('cuda:0' if torch.cuda.is_available() else 'cpu')
    loss_model = vgg19(pretrained=True).features.eval().to(device)

    dataSet = Il.ImageLoader(path=image_path, transform=transform)
    dataLoader = DataLoader(dataSet, batch_size=batch_size, shuffle=True, num_workers= 4)

    G = Sr.Generator()
    D = Sr.Discriminator()
    G.to(device)
    D.to(device)

    optimizer_G = optim.Adam(G.parameters(), lr=0.0002, betas=(0.5, 0.999))
    optimizer_D = optim.Adam(D.parameters(), lr=0.0002, betas=(0.5, 0.999))

    criterion = nn.BCELoss()

    epoch = 3

    for e in range(epoch):
        d_losses = 0.
        g_losses = 0.
        for idx, data in enumerate(dataLoader):
            real_label = torch.ones((batch_size,1)).to(device)
            fake_label = torch.zeros((batch_size,1)).to(device)
            gt, input_img = data[0].to(device), data[1].to(device)

            D_result_real = D(gt)
            D_loss_real = criterion(D_result_real, real_label)

            fakeimg = G(input_img)
            D_result_fake = D(fakeimg)
            D_loss_fake = criterion(D_result_fake, fake_label)
            D_loss = D_loss_real + D_loss_fake
            d_losses+=D_loss
            optimizer_D.zero_grad()
            D_loss.backward()
            optimizer_D.step()
            
            fakeimg = G(input_img)
            D_result_fake = D(fakeimg)
            a_loss = adv_loss(D_result_fake)
            c_loss = Ut.FeatureLoss(fakeimg, gt, loss_model, depth=3)
            G_loss = c_loss+a_loss/1000
            g_losses += G_loss
            optimizer_G.zero_grad()
            G_loss.backward()
            optimizer_G.step()

            if idx%100==99:
                print('[Epoch : %3d], [Index : %5d], [D_loss : %5f], [G_loss : %5f]'%(epoch, idx, d_losses/100, g_losses/100))
                d_losses=0.
                g_losses=0.
                tensorToImage(fakeimg)




