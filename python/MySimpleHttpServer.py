#!/usr/bin/env python

import SimpleHTTPServer
import SocketServer
import os.path
import sys

class MyRequestHandler(SimpleHTTPServer.SimpleHTTPRequestHandler):
    def do_GET(self):
        print "============================================================="
        print self.headers
        possible_name = self.path.strip("/")+'.html'
        if self.path == '/':
            self.path = '/'
        elif os.path.isfile(possible_name):
            self.path = possible_name

        r = SimpleHTTPServer.SimpleHTTPRequestHandler.do_GET(self)
#        self.send_header("Set-Cookie", "BD_HOME=0; path=/")
        return r


port = 8081
if len(sys.argv) > 1:
    try:
        p = int(sys.argv[1])
        port = p
    except ValueError:
        print "port value provided must be an integer"

print "serving on port {0}".format(port)
server = SocketServer.TCPServer(('0.0.0.0', port), MyRequestHandler)
server.serve_forever()
