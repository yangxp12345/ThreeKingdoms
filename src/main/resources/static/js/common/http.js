/**
 * 发起http请求
 * @param uri {string}  请求地址
 * @param method{string} 请求方法
 * @param data 请求体数据
 * @returns {string}
 */
function sendHttp(uri, method, data) {
    const xhr = new XMLHttpRequest();
    xhr.open(method, httpType + configHost + uri, false); // 第三个参数设置为false，表示同步请求
    const methodUpper = method.toLocaleUpperCase();
    //如果是post请求  设置请求体
    if (methodUpper === "POST") {
        xhr.setRequestHeader('Content-Type', 'application/json');//设置请求头
        xhr.send(JSON.stringify(data));//设置请求体
    } else if (methodUpper === "GET") {
        xhr.send()
    } else {
        throw "仅支持 GET/POST";
    }
    if (xhr.status === 200) {
        return JSON.parse(xhr.responseText);
    } else {
        throw new Error('请求失败: ' + xhr.statusText);
    }
}
