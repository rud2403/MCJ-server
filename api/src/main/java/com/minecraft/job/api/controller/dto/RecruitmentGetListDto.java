package com.minecraft.job.api.controller.dto;

import com.minecraft.job.common.recruitment.domain.Recruitment;
import com.minecraft.job.common.recruitment.domain.RecruitmentSearchType;
import org.springframework.data.domain.Page;

public class RecruitmentGetListDto {

    public record RecruitmentGetListRequest(
            RecruitmentSearchType searchType,
            String searchName,
            int page,
            int size,
            Long teamId
    ) {
    }

    public record RecruitmentGetListResponse(RecruitmentGetListData recruitmentGetListData) {

        public static RecruitmentGetListResponse getRecruitmentList(RecruitmentGetListData recruitmentGetListData) {
            return new RecruitmentGetListResponse(recruitmentGetListData);
        }
    }

    public record RecruitmentGetListData(Page<Recruitment> recruitmentList) {

        public static RecruitmentGetListData getRecruitmentList(Page<Recruitment> recruitmentList) {

            return new RecruitmentGetListData(recruitmentList);
        }
    }
}
