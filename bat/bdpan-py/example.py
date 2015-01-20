#!/usr/bin/env python
#coding=utf-8

import os
import sys
import json
from baidupan import BaiduPan

if __name__ == '__main__':
    action = sys.argv[1]
    p1 = sys.argv[2]
    p2 = sys.argv[3]
    p4 = sys.argv[4]
    print action
    print p1
    print p2
    access_token = p4
    disk = BaiduPan(access_token)
    if action == 'upload':
        print 'the upload result is: ' + disk.upload(p1, path='/apps/pczone'+p2)
    elif action == 'quota':
        print disk.quota()
    else:
        print 'no action'

