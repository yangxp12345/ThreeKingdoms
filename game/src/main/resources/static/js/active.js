/**
 * 移动
 * @param jsonResp 控制参数
 */
function move(jsonResp) {
    let sourceX = jsonResp["sourceX"];
    let sourceY = jsonResp["sourceY"];
    let targetX = jsonResp["targetX"];
    let targetY = jsonResp["targetY"];
    let img = document.getElementById("x" + sourceX + "_y" + sourceY).firstChild;
    document.getElementById("x" + sourceX + "_y" + sourceY).firstChild.remove();
    document.getElementById("x" + targetX + "_y" + targetY).appendChild(img)
}

/**
 * 未命中
 * @param jsonResp 控制参数
 */
function miss(jsonResp) {

}

/**
 * 击杀
 * @param jsonResp 控制参数
 */
function skill(jsonResp) {
    let targetX = jsonResp["targetX"];
    let targetY = jsonResp["targetY"];
    document.getElementById("x" + targetX + "_y" + targetY).firstChild.remove();

}

/**
 * 伤害
 * @param jsonResp 控制参数
 */
function harm(jsonResp) {
}

/**
 * 震慑
 * @param jsonResp 控制参数
 */
function frighten(jsonResp) {
}

/**
 * 力竭
 * @param jsonResp 控制参数
 */
function exhaustion(jsonResp) {
}


/**
 * 攻击
 * @param jsonResp 控制参数
 */
function act(jsonResp) {
    let sourceX = jsonResp["sourceX"];
    let sourceY = jsonResp["sourceY"];
    let targetX = jsonResp["targetX"];
    let targetY = jsonResp["targetY"];
    let firstChild = document.getElementById("x" + sourceX + "_y" + sourceY).firstChild;
    document.getElementById("x" + targetX + "_y" + targetY).firstChild;
    const element = document.getElementById("x" + targetX + "_y" + targetY).firstChild;
    blinkAndRemove(element, 100)
}

/**
 * 逃命
 * @param jsonResp 控制参数
 */
function retreat(jsonResp) {
    let sourceX = jsonResp["sourceX"];
    let sourceY = jsonResp["sourceY"];
    document.getElementById("x" + sourceX + "_y" + sourceY).firstChild;
    const element = document.getElementById("x" + sourceX + "_y" + sourceY).firstChild;
    blinkAndRemove(element, 100)
    document.getElementById("x" + sourceX + "_y" + sourceY).firstChild.remove();
}


/**
 * 闪烁
 * @param element 元素
 * @param timeoutTime 闪烁多久
 */
function blinkAndRemove(element, timeoutTime) {
    settingFlicker()
    setTimeout(function () {
        clearFlicker();
    }, timeoutTime);

    function settingFlicker() {
        element.setAttribute("class", "flicker");
    }

    function clearFlicker() {
        element.setAttribute("class", "");
    }
}
