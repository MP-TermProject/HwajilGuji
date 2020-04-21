import torchvision
import torch.nn as nn
import torch.nn.functional as F
from torchvision.models import vgg19
import copy
import torch
def GetFeature(net, result):
    features = []
    x = result
    for m in net.children():
        x = m(x)
        if isinstance(m, nn.Conv2d):
            features.append(x)
    return features