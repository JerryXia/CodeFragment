var hk_vpns = [
'HTTPS whk1.avpn.cc:44300',
'HTTPS whk2.avpn.cc:44300',
'HTTPS whk3.avpn.cc:44300',
'HTTPS whk1.avpn.cc:443'
];
var jp_vpns = [
'HTTPS wjp1.avpn.cc:44300',
'HTTPS wjp2.avpn.cc:44300',
'HTTPS wjp3.avpn.cc:44300',
'HTTPS wjp1.avpn.cc:443'
];
var tw_vpns = [
'HTTPS wtw1.avpn.cc:44300',
'HTTPS wtw2.avpn.cc:44300',
'HTTPS wtw3.avpn.cc:44300',
'HTTPS wtw4.avpn.cc:44300'
];
function FindProxyForURL(url, host) {
    var i = Math.floor(Math.random() * hk_vpns.length);
    return hk_vpns[2];
}