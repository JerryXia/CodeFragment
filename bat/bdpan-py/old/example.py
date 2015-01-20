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
    print action
    print p1
    print p2
    access_token = '21.58bb27235738272a2e0c1806b01d6275.2592000.1424255418.1979880476-479278'
    disk = BaiduPan(access_token)
    if action == 'upload':
        print 'the upload result is: '+disk.upload('/apps'+p2, p1)
    elif action == 'quota':
        print disk.quota()
    else:
        print 'no action'

