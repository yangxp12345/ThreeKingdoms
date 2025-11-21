package org.yang.business.camp.impl;

import lombok.Data;
import org.yang.business.camp.ICamp;

/**
 * 白色阵营
 */
@Data
public class WhiteImpl extends ICamp {
    final private String name = "白色";

    public WhiteImpl() {
    }

}
