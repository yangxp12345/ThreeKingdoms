let socket;//内存变量
let socketUri = "/webSocket/game";//socket连接接口
/**
 * 开启socket连接
 */
function openSocket() {

    if (typeof (WebSocket) == "undefined") {
        alert("当前浏览器不支持WebSocket协议! 请更换浏览器")
    } else {
        //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
        const socketUrl = "ws://" + configHost + socketUri + "?camp=" + document.getElementById('camp').value + "&uuid=" + getUUID("UUID");//访问SocketServer,并传输当前id
        if (socket != null) {
            socket.close();
            socket = null;
        }
        socket = new WebSocket(socketUrl);
        socket.onopen = function () {//websocket已打开
            console.log("已连接")
        };
        socket.onmessage = function (msg) {//接收消息
            const jsonResp = JSON.parse(msg.data);
            active(jsonResp);//行动
        };
        socket.onclose = function () {//WebSocket关闭事件
            console.log("断开连接")
        };
        socket.onerror = function () {//WebSocket发生了错误事件
        };
    }
}

/**
 * 通过socket发生消息给服务器
 * @param data{string}
 */
function sendMessage(data) {
    if (typeof (WebSocket) == "undefined") {//您的浏览器不支持WebSocket
    } else {
        socket.send(data);
    }
}