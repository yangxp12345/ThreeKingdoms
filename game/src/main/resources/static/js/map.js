/**
 * 加载地图
 */
function map() {
    /**
     * 渲染地图和角色
     * @type {string}
     */

    const jsonResp = sendHttp("/run/init", "POST", {});

    const map = document.getElementById("map");
    let grids = jsonResp["grids"];
    let yRangeMax = grids[0].length - 1;//y的最大值
    for (let y = yRangeMax; y >= 0; y--) {
        const divLine = document.createElement("div");
        map.appendChild(divLine)
        divLine.style.display = "flex";
        for (let x = 0; x < grids.length; x++) {
            const divColumn = document.createElement("div");
            divLine.appendChild(divColumn)
            divColumn.setAttribute("id", "x" + x + "_y" + y);
            if (grids[x][y]["name"] === "平原") {
                divColumn.style.backgroundImage = "url('../img/plain.png')";
                divColumn.style.width = "30px";
                divColumn.style.height = "30px";
            } else {

            }
        }
    }
    /**
     * 放入大本营
     */

    let campLocation = jsonResp["campLocation"];

    for (let i = 0; i < campLocation.length; i++) {
        let unitLocation = campLocation[i];
        let x = unitLocation["x"];
        let y = unitLocation["y"];
        document.getElementById("x" + x + "_y" + y).style.backgroundImage = "url('../img/location.png')";
        let camp = unitLocation["camp"]["name"];
        let hash = camp.hashCode();
        document.getElementById("x" + x + "_y" + y).style.filter = "hue-rotate(" + hash % configFilter + "deg)"

    }


    /**
     * 放入角色
     */
    let roleModels = jsonResp["roleModels"];
    for (let i = 0; i < roleModels.length; i++) {
        let roleModel = roleModels[i];
        let x = roleModel["x"];
        let y = roleModel["y"];
        let camp = roleModel["camp"]["name"];//阵营
        let name = roleModel["roleType"]["name"];//身份
        let health = roleModel["health"];//生命
        let hash = camp.hashCode();

        let img = document.createElement("img");
        if (name === "主将") {
            img.src = "../img/general.png";
        } else if (name === "副将") {
            img.src = "../img/deputy.png";
        } else {
            img.src = "../img/soldier.png";
        }

        img.style.filter = "hue-rotate(" + hash % configFilter + "deg)"
        img.setAttribute("roleId", roleModel["id"]);
        if (camp === "黑色") {
            img.style.transform = 'scaleX(-1)';
        }
        document.getElementById("x" + x + "_y" + y).appendChild(img)
    }
}


/**
 * 启动白兵战
 */
function start() {
    sendHttp("/run/start", "POST", {});
}

function setSleep() {
    let sleep = document.getElementById("sleep").value;
    sendHttp("/run/sleep", "POST", {sleep: sleep});
}


/**
 * 角色的行动动作
 * @param jsonResp
 */
function active(jsonResp) {
    const name = jsonResp["name"];
    switch (name) {
        case "移动":
            move(jsonResp);
            break;
        case "未命中":
            miss(jsonResp);
            break;
        case "击杀":
            skill(jsonResp);
            break;
        case "伤害":
            harm(jsonResp);
            break;
        case "震慑":
            frighten(jsonResp);
            break;
        case "力竭":
            exhaustion(jsonResp);
            break;
        case "攻击":
            act(jsonResp);
            break;
    }
}

