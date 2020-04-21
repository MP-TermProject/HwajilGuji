import torch.nn as nn
import torch.nn.functional as F

class BlockBn(nn.Module):
    def __init__(self, block, input_c, output_c, kernel_size=3, stride=1, padding=1):
        super(BlockBn, self).__init__()
        self.main = nn.Sequential(
            block(input_c, output_c, kernel_size, stride=stride, padding=padding),
            nn.BatchNorm2d(output_c),
        )
    def forward(self, input):
        return self.main(input)

class ResidualBlock(nn.Module):
    def __init__(self, channel, activation = nn.ReLU(inplace=True)):
        super(ResidualBlock, self).__init__()
        self.main = nn.Sequential(
            BlockBn(nn.Conv2d, channel, channel),
            activation,
            BlockBn(nn.Conv2d, channel, channel)
        )
    def forward(self, input):
        return self.main(input) + input

class UNet(nn.Module):
    def __init__(self, depth, input_c, res_num=3, activation = nn.ReLU(inplace=True)):
        super(UNet, self).__init__()
        channel = input_c
        self.up_seq = nn.ModuleList()
        self.down_seq = nn.ModuleList()
        residual = nn.ModuleList()
        for i in range(depth):
            self.down_seq.append(
                nn.Sequential(
                    BlockBn(nn.Conv2d, channel, channel*2, kernel_size=4, stride=2, padding=1),
                    activation
                )
            )
            channel *= 2
        for i in range(res_num):
            residual.append(
                nn.Sequential(
                    ResidualBlock(channel, activation=activation),
                    activation
                )
            )
        self.res_seq = nn.Sequential(* residual)
        for i in range(depth):
            self.up_seq.append(
                nn.Sequential(
                    BlockBn(nn.ConvTranspose2d, channel, channel//2, kernel_size=4, stride=2, padding=1),
                    activation
                )
            )
            channel =channel// 2

    def forward(self, x):
        d_sample = [x,]
        for m in self.down_seq:
            x= m(x)
            d_sample.append(x)
        x = self.res_seq(x)
        for idx, m in enumerate(self.up_seq):
            x = m(x)
            x += d_sample[-(idx+2)]
        return x
'''
import torch
dummy_img = torch.randn((1, 32, 256, 256))
net = UNet(3, 32, res_num=12)
result = net(dummy_img)
print(result.shape)
'''