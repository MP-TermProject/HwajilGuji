import torch
import torchvision
import os
import PIL.Image as Image
import random
from torch.utils.data import DataLoader

class ImageLoader(DataLoader):
    def __init__(self, path = '/temp', transform = None):
        self.path = path
        self.transform = transform
        self.img_list = os.listdir(path)

    def __len__(self):
        return len(self.igm_list)

    def __getitem__(self, idx):
        fname = self.img_list[idx]
        img = Image.open(os.path.join(self.path, fname))
        w, h = img.size
        w = w//8*8
        h = h//8*8
        img = img.resize((w, h))
        randval = random.randrange(2,4)
        small_img = img.resize((int(w/randval), int(h/randval)))
        small_img = small_img.resize((w, h))
        if self.transform is not None:
            img = self.transform(img)
            small_img = self.transform(small_img)
        return img, small_img

path = 'C:/Users/kjm04/Downloads/celeba-dataset/img_align_celeba/img_align_celeba'
loader = ImageLoader(path=path)
img, small_img = loader.__getitem__(1)
import matplotlib.pyplot as plt
plt.imshow(img)
plt.show()
plt.imshow(small_img)
plt.show()