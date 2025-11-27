package org.yang.business.role.soldier;

import lombok.Getter;

/**
 * 士兵角色数值
 */
@Getter
public enum SoldierDataEnum {
    SoldierData1("1", 10, 1, 10, 0, 0, 10, 0, 1, 1),
    SoldierData2("2", 20, 2, 20, 0, 0, 10, 1, 1, 2),
    SoldierData3("3", 50, 4, 50, 20, 0, 20, 2, 2, 3),
    SoldierData4("4", 100, 6, 100, 30, 0, 40, 3, 3, 4),
    SoldierData5("5", 150, 10, 100, 40, 1, 50, 5, 4, 5),
    SoldierData6("6", 200, 15, 120, 50, 1, 60, 8, 5, 6),
    SoldierData7("7", 300, 25, 180, 50, 2, 80, 8, 8, 7),
    SoldierData8("8", 500, 40, 200, 100, 3, 100, 10, 10, 8),
    SoldierData9("9", 800, 60, 250, 100, 5, 120, 10, 12, 9),
    SoldierData10("10", 1200, 90, 350, 150, 8, 160, 15, 14, 10),
    SoldierData11("11", 1500, 120, 500, 200, 10, 180, 15, 16, 11),
    SoldierData12("12", 2000, 150, 800, 240, 15, 200, 18, 18, 12),
    SoldierData13("13", 3000, 200, 1000, 300, 20, 250, 20, 20, 13),
    SoldierData14("14", 5000, 300, 1300, 500, 25, 300, 20, 24, 14),
    SoldierData15("15", 8000, 500, 1500, 600, 35, 330, 40, 28, 15),
    SoldierData16("16", 10000, 800, 1800, 800, 40, 380, 50, 30, 16),
    SoldierData17("17", 15000, 1000, 2000, 1000, 45, 400, 60, 35, 17),
    SoldierData18("18", 25000, 1500, 2500, 1400, 50, 500, 80, 38, 18),
    SoldierData19("19", 40000, 2000, 2800, 1600, 60, 600, 100, 40, 19),
    SoldierData20("20", 60000, 3000, 3000, 1800, 70, 700, 100, 45, 20),
    SoldierData21("21", 100000, 5000, 4000, 2000, 80, 800, 120, 48, 21),
    SoldierData22("22", 150000, 8000, 5000, 2500, 90, 900, 150, 50, 22),
    SoldierData23("23", 200000, 10000, 6000, 2600, 100, 1000, 180, 53, 23),
    SoldierData24("24", 300000, 12000, 8000, 2800, 120, 1200, 200, 57, 24),
    SoldierData25("25", 500000, 16000, 10000, 3000, 140, 1300, 250, 61, 25),
    SoldierData26("26", 1000000, 20000, 12000, 4000, 150, 1500, 300, 64, 26),
    SoldierData27("27", 2000000, 25000, 14000, 5000, 180, 1800, 400, 68, 27),
    SoldierData28("28", 3000000, 35000, 18000, 6000, 200, 2000, 500, 70, 28),
    SoldierData29("29", 5000000, 50000, 20000, 8000, 250, 2500, 800, 73, 29),
    SoldierData30("30", 10000000, 80000, 24000, 9000, 300, 2800, 1000, 75, 30),
    SoldierData31("31", 20000000, 100000, 30000, 10000, 400, 3000, 1500, 76, 32),
    SoldierData32("32", 50000000, 120000, 50000, 20000, 500, 4000, 1800, 80, 35),
    SoldierData33("33", 100000000, 200000, 100000, 50000, 1000, 5000, 2000, 90, 40),
    ;

    final private String name;//角色名称
    final private int health;//生命值
    final private int attack;//攻击力
    final private int trick;//技巧 技巧一般都比较高
    final private int defense;//防御力
    final private int exempt;//伤害最终减免
    final private int hit;//命中
    final private int dodge;//闪避
    final private int unity;//胆魄
    final private int act;//行动

    SoldierDataEnum(String name, int health, int attack, int trick, int defense, int exempt, int hit, int dodge, int unity, int act) {
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.trick = trick;
        this.defense = defense;
        this.exempt = exempt;
        this.hit = hit;
        this.dodge = dodge;
        this.unity = unity;
        this.act = act;
    }

}
