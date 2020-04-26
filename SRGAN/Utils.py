import torchvision
import torch.nn as nn
import torch.nn.functional as F
from torchvision.models import vgg19
import copy
import torch
import numpy as np
def GetFeature(net, result):
    features = []
    x = result
    for m in net.children():
        x = m(x)
        if isinstance(m, nn.Conv2d):
            features.append(x)
    return np.array(features)

def FeatureLoss(result, gt, net, depth=5):
    gt = GetFeature(net, gt)
    result = GetFeature(net, result)
    add_val=0.
    for i in range(depth):
        add_val+=((gt[i]-result[i])**2).mean()
        print(add_val)
    return add_val/depth
'''
device = torch.device('cuda:0' if torch.cuda.is_available() else 'cpu')
network = vgg19(pretrained=True).features.eval().to(device)
#print(network)
abc = torch.randn(3,3,256,256).to(device)
efg = torch.randn(3,3,256,256).to(device)
print(FeatureLoss(abc, efg, network))
'''
'''
res = GetFeature(network,abc)
res2 = GetFeature(network, efg)
depth = []
add_val = 0.
for i in range(3):
    qwert = (res[i]-res2[i])**2
    depth.append(qwert.mean())
    add_val+=qwert.mean()
print(depth[0].shape)
print(type(depth[0]))
print(add_val)
'''