#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import unicode_literals

import os
# from StringIO import StringIO
import time
# from PIL import Image

from baidupcs import PCS, InvalidToken
#from .utils import content_md5, content_crc32, slice_md5

access_token = '21.58bb27235738272a2e0c1806b01d6275.2592000.1424255418.1979880476-479278'
pcs = PCS(access_token)

verify = True
# verify = False  # 因为在我电脑上会出现 SSLError 所以禁用 https 证书验证


def _file(filename):
    current_dir = os.path.dirname(os.path.abspath(__file__))
    filepath = os.path.join(current_dir, filename)
    f = open(filepath, 'rb')  # rb 模式
    return f

def test_info():
    """磁盘配额信息"""
    response = pcs.info()
    print response.json()


def test_upload():
    """上传"""
    response = pcs.upload('/apps/pczone/test.txt', _file('test1'),
                          ondup='overwrite', verify=verify)
    print response.json()

    response = pcs.upload('/apps/pczone/test.txt', _file('test1'),
                          ondup='overwrite', verify=verify,
                          headers={'Accept': '*/*'})
    print response.json()




