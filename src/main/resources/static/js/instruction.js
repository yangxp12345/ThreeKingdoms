/**
 * 批量修改指令
 * @param instructionName 攻击类型
 * @param roleTypeName 角色类型
 */
function instructionBatch(instructionName, roleTypeName) {
    const camp = document.getElementById("camp").value;
    const data = {
        campName: camp,
        instructionName: instructionName,
        roleTypeName: roleTypeName
    };
    sendHttp("/run/instructionBatch", "POST", data);

}


/**
 * 修改单个指令
 * @param instructionName
 */
function instruction(instructionName) {


    const roleId = document.getElementById("roleId").innerText;
    if (roleId === '') {
        alert("请在地图上点击角色锁定需要控制的人!")
        return;
    }
    const data = {
        instructionName: instructionName,
        roleId: roleId
    };
    sendHttp("/run/instruction", "POST", data);
}