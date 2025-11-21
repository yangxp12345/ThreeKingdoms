package org.yang.business.grid;

import org.yang.business.grid.impl.PlainImpl;

/**
 * 地图数据实例
 */
public class ExampleGrid {

    public static void defaultGrid(IGrid[][] grids) {
        int maxY = grids[0].length;
        int maxX = grids.length;
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                grids[x][y] = IGrid.classMap.get(PlainImpl.class);
            }
        }
    }

}
