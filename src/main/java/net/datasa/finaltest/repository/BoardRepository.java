package net.datasa.finaltest.repository;

import net.datasa.finaltest.entity.BoardEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    // 카테고리별 조회 및 날짜 내림차순 정렬
    List<BoardEntity> findByCategoryOrderByCreateDateDesc(String category);
}