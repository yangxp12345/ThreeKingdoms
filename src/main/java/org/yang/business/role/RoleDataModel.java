package org.yang.business.role;

import lombok.Data;

/**
 * 角色数据模型
 */
@Data
public class RoleDataModel {
    private int commander;//统帅
    private int health;//生命值
    private int attack;//攻击力
    private int defense;//防御力
    private int exempt;//伤害最终减免
    private int hit;//命中
    private int dodge;//闪避
    private int trick;//技巧 技巧一般都比较高
    private int unity;//胆魄
    private int act;//行动

    /**
     * 使用默认数据
     *
     * @return 返回数值对象
     */
    public static RoleDataModel getRoleDataModel() {
        return new RoleDataModel();
    }

    private RoleDataModel() {//角色默认数据
        this(
                100,
                100,
                20,
                1,
                1,
                10,
                1,
                100,
                5,
                5
        );
    }

    public RoleDataModel(int commander, int health, int attack, int defense, int exempt, int hit, int dodge, int trick, int unity, int act) {
        this.commander = commander;//统帅
        this.health = health;//生命值
        this.attack = attack;//攻击力
        this.defense = defense;//防御力
        this.exempt = exempt;//伤害最终减免
        this.hit = hit;//命中
        this.dodge = dodge;//闪避
        this.trick = trick;//技巧 技巧一般都比较高
        this.unity = unity;//胆魄
        this.act = act;//行动
    }
}