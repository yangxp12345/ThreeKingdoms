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

        /**
         * 添加点击时间  单独控制某一个角色
         */
        img.addEventListener('click', function () {
            document.getElementById("roleId").innerText = this.getAttribute("roleId");
        })

        /**
         * 添加移入事件 自动查询当前角色id的信息
         */
        img.addEventListener('mouseenter', function () {
            const jsonResp = sendHttp("/run/getRoleIdMsg", "POST", {roleId: this.getAttribute("roleId")});
            let attack = jsonResp["attack"];//攻击力
            let camp = jsonResp["camp"]["name"];//阵容名称
            let command = jsonResp["command"]["name"];//当前角色指令
            let cumulativeActive = jsonResp["cumulativeActive"];//最大行动力
            let currentActive = jsonResp["currentActive"];//当前行动力
            let cumulativeHealth = jsonResp["cumulativeHealth"];//角色最大生命值
            let currentHealth = jsonResp["currentHealth"];//角色当前生命值
            let defense = jsonResp["defense"];//护甲
            let dodge = jsonResp["dodge"];//闪避
            let exempt = jsonResp["exempt"];//免伤
            let grid = jsonResp["grid"]["name"];//路面
            let act = jsonResp["grid"]["act"];//路面消耗
            let hit = jsonResp["hit"];//命中
            let id = jsonResp["id"];//角色id
            let roleType = jsonResp["roleType"]["name"];//角色类型
            let trick = jsonResp["trick"];//技巧
            let unity = jsonResp["unity"];//胆魄
            let weapon = jsonResp["weapon"]["name"];//武器
            let active = jsonResp["weapon"]["active"];//武器使用消耗
            let x = jsonResp["x"];//坐标x
            let y = jsonResp["y"];//坐标y

            document.getElementById("campType").innerText = camp;
            document.getElementById("roleType").innerText = roleType;
            document.getElementById("command").innerText = command;
            document.getElementById("health").innerText = currentHealth + "/" + cumulativeHealth;
            document.getElementById("active").innerText = currentActive + "/" + cumulativeActive;
            document.getElementById("weapon").innerText = weapon + ":[" + active + "]";
            document.getElementById("grid").innerText = grid + ":[" + act + "]";
            document.getElementById("coordinate").innerText = "(" + x + "," + y + ")";
            document.getElementById("attack").innerText = attack;
            document.getElementById("trick").innerText = trick;
            document.getElementById("defense").innerText = defense;
            document.getElementById("exempt").innerText = exempt;
            document.getElementById("hit").innerText = hit;
            document.getElementById("dodge").innerText = dodge;
            document.getElementById("unity").innerText = unity;
        });

        document.getElementById("x" + x + "_y" + y).appendChild(img)
    }
}


/**
 * 启动白兵战
 */
function start() {
    openSocket();//开启socket
    sendHttp("/run/start", "POST", {});
}

/**
 * 设置角色停顿时间
 */
function setSleep() {
    let sleep = document.getElementById("sleep").value;
    sendHttp("/run/sleep", "POST", {sleep: sleep});
}


/**
 * 加载默认角色
 */
function initCamp() {
    const camp = document.getElementById('camp').value;
    if (getCookie("camp") === null) {
        setCookie("camp", camp);
    } else {
        const camp = document.getElementById('camp');
        camp.value = getCookie("camp"); // 设置默认选中的项的value值
    }
}

function updateCamp() {
    setCookie("camp", document.getElementById('camp').value);
    openSocket();//加载web连接
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
        case "撤退":
            retreat(jsonResp);
            break;
    }
}

