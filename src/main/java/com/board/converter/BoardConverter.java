package com.board.converter;

import com.board.dto.BoardModifyRequest;
import com.board.dto.BoardWriteRequest;
import com.board.dto.BoardsRequest;
import com.board.dto.ReplyWriteRequest;
import com.board.vo.BoardVO;
import com.board.vo.ReplyVO;
import com.board.vo.SearchVO;
import org.mapstruct.Mapper;

import java.time.LocalDate;

@Mapper(componentModel = "spring")// MapStruct의 @Mapper → 객체 변환용 (MyBatis SQL 매핑용과 다름)
// 생성된 구현체를 spring 빈으로 등록, @Autowired로 주입 가능하게 함
public interface BoardConverter { // 이 맵퍼만 추가하면 필드명이 같으면 자동으로 매핑됨 (빌드 시점에 구현체가 만들어짐), 호출하는 쪽에서 인자 타입에 따라 컴파일러가 알아서 골라줌
    // dto -> entity(vo) 전환용 맵퍼
    BoardVO from(BoardWriteRequest request);
    BoardVO from(BoardModifyRequest request);
    ReplyVO from(ReplyWriteRequest request);
    // default : 인터페이스에 기본 구현 제공, 구현 클래스가 안 만들어도 됨
    default SearchVO from(BoardsRequest request) { //  BoardsRequest → SearchVO로 타입 변환 + null 처리 로직
        SearchVO vo = new SearchVO();
        LocalDate today = LocalDate.now();
        String startDateFallback = today.minusYears(1).toString();
        String endDateFallback = today.toString();
        vo.setStartDate((request.getStartDate() == null || request.getStartDate().isEmpty())
                ? startDateFallback : request.getStartDate());
        vo.setEndDate((request.getEndDate() == null || request.getEndDate().isEmpty())
                ? endDateFallback : request.getEndDate());
        vo.setCategoryId(request.getCategoryId() != null ? request.getCategoryId() : 0);
        vo.setKeyword(request.getKeyword());
        vo.setPage(request.getPage() != null ? request.getPage() : 1);
        vo.setPageSize(request.getPageSize() != 0 ? request.getPageSize() : 10);
        return vo;
    }
}
