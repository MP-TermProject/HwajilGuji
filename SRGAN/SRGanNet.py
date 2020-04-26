import torch.nn as nn
import torch.nn.functional as F
import Network as net
import torch
class Generator(nn.Module):
    def __init__(self):
        super(Generator, self).__init__()
        self.main1 = nn.Sequential(
            nn.Conv2d(3, 64, 9, stride=1, padding=4),
            nn.PReLU(),
        )
        self.main2 = nn.Sequential(
            net.ResidualBlock(64, activation = nn.PReLU()),
            net.ResidualBlock(64, activation = nn.PReLU()),
            net.ResidualBlock(64, activation = nn.PReLU()),
            net.BlockBn(nn.Conv2d, 64, 64, 3, stride=1, padding=1))
        self.lastConv = nn.Sequential(
            nn.PReLU(),
            nn.Conv2d(64, 3, 9, stride=1, padding=4)
        )

    def forward(self, input):
        x = self.main1(input)
        x = self.main2(x) + x
        x = self.lastConv(x)
        return x

class Discriminator(nn.Module):
    def __init__(self):
        super(Discriminator, self).__init__()
        self.main = nn.Sequential(
            nn.Conv2d(3, 64, 3, stride=1, padding=1),
            nn.LeakyReLU(0.2, inplace=True),
            net.BlockBn(nn.Conv2d, 64, 64, 4, stride=2, padding=1),
            nn.LeakyReLU(0.2, inplace=True),
            net.BlockBn(nn.Conv2d, 64, 128, 4, stride=1, padding=1),
            nn.LeakyReLU(0.2, inplace=True),
            net.BlockBn(nn.Conv2d, 128, 128, 3, stride=2, padding=1),
            nn.LeakyReLU(0.2, inplace=True),
            net.BlockBn(nn.Conv2d, 128, 256, 4, stride=1, padding=1),
            nn.LeakyReLU(0.2, inplace=True),
            net.BlockBn(nn.Conv2d, 256, 256, 3, stride=2, padding=1),
            nn.LeakyReLU(0.2, inplace=True),
            net.BlockBn(nn.Conv2d, 256, 512, 4, stride=1, padding=1),
            nn.LeakyReLU(0.2, inplace=True),
            net.BlockBn(nn.Conv2d, 512, 512, 3, stride=2, padding=1),
            nn.LeakyReLU(0.2, inplace=True),
        )
        size = 2048
        self.main2 = nn.Sequential(
            nn.Linear(size ,1024),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Linear(1024, 1),
            nn.Sigmoid()
        )
    def forward(self, input):
        x = self.main(input)
        x_flat = x.view(-1,2048)
        x = self.main2(x_flat)
        return x
