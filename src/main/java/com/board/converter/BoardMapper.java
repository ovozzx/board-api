package com.board.converter;

import com.board.dto.BoardModifyRequest;
import com.board.dto.BoardWriteRequest;
import com.board.vo.BoardVO;
import org.mapstruct.Mapper;

@Mapper // // MapStruct의 @Mapper → 객체 변환용 (MyBatis SQL 매핑용과 다름)
public interface BoardMapper { // 이 맵퍼만 추가하면 필드명이 같으면 자동으로 매핑됨
    // dto -> entity(vo) 전환용 맵퍼
    BoardVO from(BoardWriteRequest request);
    BoardVO from(BoardModifyRequest request);

}
