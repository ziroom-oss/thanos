package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmegaPageInfo<T> {
    private List<T> list;
    private Integer total;
    private Integer pageNumber;
    private Integer pageSize;
}
