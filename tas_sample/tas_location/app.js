
var net = require('net');
var util = require('util');
var fs = require('fs');
var xml2js = require('xml2js');

var wdt = require('./wdt');

const path = require('path');

const moment = require('moment');
require('moment-timezone');
moment.tz.setDefault("Asia/Seoul");

var useparentport = '';
var useparenthostname = '';
var upload_arr = [];
var download_arr = [];
var conf = {};

// This is an async file read
//conf.xml 파일 읽어서 data 파싱하는 부분, xml을 자바스크립트 파싱
fs.readFile('conf.xml', 'utf-8', function (err, data) {
    if (err) {
        console.log("FATAL An error occurred trying to read in the file: " + err);
        console.log("error : set to default for configuration")
    }
    else {
        var parser = new xml2js.Parser({explicitArray: false});
        parser.parseString(data, function (err, result) {
            if (err) {
                console.log("Parsing An error occurred trying to read in the file: " + err);
                console.log("error : set to default for configuration")
            }
            else {
                //stringfy는 자바스크립트 값을 JSON 문자열로 변환
                //JSON.stringfy(value, replacer, space): value만 필수
                var jsonString = JSON.stringify(result);
                conf = JSON.parse(jsonString)['m2m:conf'];

                //conf.xml의 parenthostname 사용하는 부분, thyme.js로 연결하는것
                useparenthostname = conf.tas.parenthostname;
                useparentport = conf.tas.parentport;

                if(conf.upload != null) {
                    if (conf.upload['ctname'] != null) {
                        //여기로 안 들어옴
                        upload_arr[0] = conf.upload;

                    }
                    else {
                        upload_arr = conf.upload;
                        console.log('upload_arr -> conf.upload :' + upload_arr[0]);
                    }
                }
            }
        });
    }
});


var tas_state = 'init';

var upload_client = null;

var t_count = 0;

setInterval(() => {
    if (tas_state == 'upload') {
        var now = new Date();
        var second = 1000 * 60;
        var fmt1 = 'YYYYMMDDHHmmss';
        var ct = moment(now).format(fmt1); //Date 객체를 파라미터로 넣기
        console.log('**TIME: ' + ct);

        var hi = (Math.random() * 10) + 1;
        console.log('hi == :' + hi);

        var a = '12345';
        var b = 12345;

        var util = require('util');
        var data = util.format('%010d, %s', b, a);

        console.log('****** result : ' +  data);

        console.log('test----------------------------');

        //종류:
        // (1) %014d : 정수 14자리, 앞을 0으로
        // (2) %010.4f : 정수 10자리, 앞을 0으로 + 소수4자리
        // (3) %010d : 정수 10자리, 앞을 0으로
        // (4) %011d : 정수 11자리, 앞을 0으로


        function makeRandom(min, max){
            var RandVal = Math.floor(Math.random()*(max-min+1)) + min;
            return RandVal;
        }


        function padLeft(nr, n, str){
            return Array(n-String(nr).length+1).join(str||'0')+nr;
        }

//or as a Number prototype method:

        Number.prototype.padLeft = function(n,str){
            return Array(n-String(this).length+1).join(str||'0')+this;
        }


        console.log('test----------------------------');


        var type1 = padLeft(Math.floor(Math.random() * 1000) + 1,14);       //=> (1)
        var type2 = padLeft(Math.floor(Math.random() * 1000) + 1,10) + '.' + padLeft(makeRandom(0, 9999), 4);    //=> (2)
        var type3 = padLeft(Math.floor(Math.random() * 1000) + 1,10);       //=> (3)
        var type4 = padLeft(Math.floor(Math.random() * 1000) + 1,11);       //=> (4)

        var con = {
            time: ct,
            x: type2,
            y: type2,
            z: type2
        };

        for (var i = 0; i < upload_arr.length; i++) {
			if (upload_arr[i].id == 'data') {
                var cin = {ctname: upload_arr[i].ctname, con: con};
                console.log(JSON.stringify(cin) + ' ---->');
                upload_client.write(JSON.stringify(cin) + '<EOF>');
                continue;
            }
        }
    }
}, 500);

function on_receive(data) {
    if (tas_state == 'connect' || tas_state == 'reconnect' || tas_state == 'upload') {
        var data_arr = data.toString().split('<EOF>');
        if(data_arr.length >= 2) {
            for (var i = 0; i < data_arr.length - 1; i++) {
                var line = data_arr[i];
                var sink_str = util.format('%s', line.toString());
                var sink_obj = JSON.parse(sink_str);

                if (sink_obj.ctname == null || sink_obj.con == null) {
                    console.log('Received: data format mismatch');
                }
                else {
                    if (sink_obj.con == 'hello') {
                        console.log('Received: ' + line);

                        if (++tas_download_count >= download_arr.length) {
                            tas_state = 'upload';
                        }
                    }
                    else {
                        for (var j = 0; j < upload_arr.length; j++) {
                            if (upload_arr[j].ctname == sink_obj.ctname) {
                                console.log('ACK : ' + line + ' <----');
                                break;
                            }
                        }

                        for (j = 0; j < download_arr.length; j++) {
                            if (download_arr[j].ctname == sink_obj.ctname) {
                                g_down_buf = JSON.stringify({id: download_arr[i].id, con: sink_obj.con});
                                console.log(g_down_buf + ' <----');
                                myPort.write(g_down_buf);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}


var myPort = null;
function tas_watchdog() {
    if(tas_state == 'init') {
        upload_client = new net.Socket();

        upload_client.on('data', on_receive);

        upload_client.on('error', function(err) {
            console.log(err);
            tas_state = 'reconnect';
        });

        upload_client.on('close', function() {
            console.log('Connection closed');
            upload_client.destroy();
            tas_state = 'reconnect';
        });

        if(upload_client) {
            console.log('tas init ok');
            tas_state = 'init_serial';
        }
    }
    else if(tas_state == 'init_serial') {
        tas_state = 'connect';
    }
    else if(tas_state == 'connect' || tas_state == 'reconnect') {
        upload_client.connect(useparentport, useparenthostname, function() {
            console.log('upload Connected');
            tas_download_count = 0;
            tas_state = 'upload';

        });
    }
}

wdt.set_wdt(require('shortid').generate(), 3, tas_watchdog);