package com.board.converter;

import com.board.dto.BoardModifyRequest;
import com.board.dto.BoardWriteRequest;
import com.board.dto.ReplyWriteRequest;
import com.board.vo.BoardVO;
import com.board.vo.ReplyVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")// MapStruct의 @Mapper → 객체 변환용 (MyBatis SQL 매핑용과 다름)
// 생성된 구현체를 spring 빈으로 등록, @Autowired로 주입 가능하게 함
public interface BoardConverter { // 이 맵퍼만 추가하면 필드명이 같으면 자동으로 매핑됨 (빌드 시점에 구현체가 만들어짐)
    // dto -> entity(vo) 전환용 맵퍼
    BoardVO from(BoardWriteRequest request);
    BoardVO from(BoardModifyRequest request);
    ReplyVO from(ReplyWriteRequest request);

}
