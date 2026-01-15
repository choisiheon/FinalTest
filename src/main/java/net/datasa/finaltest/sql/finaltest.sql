create schema springJPA;

use springJPA;

DROP TABLE IF EXISTS community_group;
DROP TABLE IF EXISTS community_board;
DROP TABLE IF EXISTS community_member;

CREATE TABLE community_member
(
    member_id VARCHAR(20) PRIMARY KEY COMMENT '회원 아이디',
    member_pw VARCHAR(100) NOT NULL COMMENT '비밀번호(암호화)',
    name      VARCHAR(20)  NOT NULL COMMENT '이름',
    email     VARCHAR(200) COMMENT '이메일'
);
SELECT *FROM community_member;


CREATE TABLE community_board
(
    board_num   INT AUTO_INCREMENT PRIMARY KEY COMMENT '글 번호',
    member_id   VARCHAR(20) COMMENT '작성자 아이디',
    category    VARCHAR(50)   NOT NULL COMMENT '카테고리(STUDY/HEALTHY/TRIP)',
    title       VARCHAR(200)  NOT NULL COMMENT '제목',
    contents    VARCHAR(2000) NOT NULL COMMENT '내용',
    headcnt     INT          DEFAULT 0 COMMENT '현재 인원 수',
    capacity    INT          DEFAULT 5 COMMENT '최대 정원 수',
    status      VARCHAR(100) DEFAULT 'OPEN' COMMENT '모집 상태(OPEN/CLOSED)',
    create_date TIMESTAMP    DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',

    CONSTRAINT fk_board_member FOREIGN KEY (member_id)
        REFERENCES community_member (member_id) ON DELETE SET NULL
);
select * from community_board;

CREATE TABLE community_group
(
    group_id    INT AUTO_INCREMENT PRIMARY KEY COMMENT '참여 번호',
    board_num   INT COMMENT '참여 모집 글 번호',
    member_id   VARCHAR(20) COMMENT '참여 회원 아이디',
    role        VARCHAR(20) DEFAULT 'MEMBER' COMMENT '역할(LEADER/MEMBER)',
    status      VARCHAR(20) DEFAULT 'PENDING' COMMENT '상태(PENDING/JOINED/REJECTED)',
    joined_date TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT '참여/신청 일시',

    CONSTRAINT fk_group_board FOREIGN KEY (board_num)
        REFERENCES community_board (board_num) ON DELETE CASCADE,

    CONSTRAINT fk_group_member FOREIGN KEY (member_id)
        REFERENCES community_member (member_id) ON DELETE CASCADE
);
select * from community_group;


-- 1. 회원 데이터 생성 (비밀번호는 모두 '1234'로 암호화된 값입니다)
INSERT INTO community_member (member_id, member_pw, name, email)
VALUES
    ('user1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGqqzdgow.9PBqvIcJGflTLPFxX57q', '김철수', 'user1@test.com'),
    ('user2', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGqqzdgow.9PBqvIcJGflTLPFxX57q', '이영희', 'user2@test.com'),
    ('user3', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGqqzdgow.9PBqvIcJGflTLPFxX57q', '박민수', 'user3@test.com');


-- 2. 게시판 데이터 생성
-- 1번 글: user1이 작성, STUDY, 모집 중 (현재 2명 / 정원 5명)
INSERT INTO community_board (board_num, member_id, category, title, contents, headcnt, capacity, status, create_date)
VALUES (1, 'user1', 'STUDY', '일본어 중급 이상 스터디 모집', 'JLPT N2 이상 목표로 하시는 분 구합니다. 매주 토요일 오전 10시 강남역.', 2, 5, 'OPEN', '2026-01-12 12:30:00');

-- 2번 글: user2가 작성, HEALTHY, 모집 중 (현재 1명 / 정원 10명)
INSERT INTO community_board (board_num, member_id, category, title, contents, headcnt, capacity, status, create_date)
VALUES (2, 'user2', 'HEALTHY', '매일 아침 런닝 크루', '한강 공원에서 매일 아침 7시에 30분 런닝하실 분!', 1, 10, 'OPEN', '2026-01-14 09:00:00');

-- 3번 글: user1이 작성, TRIP, 모집 마감 (현재 2명 / 정원 2명)
INSERT INTO community_board (board_num, member_id, category, title, contents, headcnt, capacity, status, create_date)
VALUES (3, 'user1', 'TRIP', '2월 부산 여행 동행 구함', '2박 3일 부산 여행 같이 가실 분 딱 한 분만 구해요.', 2, 2, 'CLOSED', '2026-01-15 10:00:00');


-- 3. 모임 참여/신청 데이터 생성 (community_group)

-- [1번 글 관련] 일본어 스터디
-- user1 (주최자) -> LEADER, JOINED
INSERT INTO community_group (board_num, member_id, role, status, joined_date)
VALUES (1, 'user1', 'LEADER', 'JOINED', '2026-01-12 12:30:00');

-- user2 (참여자) -> MEMBER, JOINED (이미 승인됨)
INSERT INTO community_group (board_num, member_id, role, status, joined_date)
VALUES (1, 'user2', 'MEMBER', 'JOINED', '2026-01-12 14:00:00');

-- user3 (신청자) -> MEMBER, PENDING (승인 대기 중 -> '확인하기' 버튼 테스트용)
INSERT INTO community_group (board_num, member_id, role, status, joined_date)
VALUES (1, 'user3', 'MEMBER', 'PENDING', '2026-01-13 10:00:00');


-- [2번 글 관련] 런닝 크루
-- user2 (주최자) -> LEADER, JOINED
INSERT INTO community_group (board_num, member_id, role, status, joined_date)
VALUES (2, 'user2', 'LEADER', 'JOINED', '2026-01-14 09:00:00');


-- [3번 글 관련] 부산 여행 (마감됨)
-- user1 (주최자) -> LEADER, JOINED
INSERT INTO community_group (board_num, member_id, role, status, joined_date)
VALUES (3, 'user1', 'LEADER', 'JOINED', '2026-01-15 10:00:00');

-- user3 (참여자) -> MEMBER, JOINED (승인 완료되어 정원이 참 -> CLOSED 상태 확인용)
INSERT INTO community_group (board_num, member_id, role, status, joined_date)
VALUES (3, 'user3', 'MEMBER', 'JOINED', '2026-01-15 11:00:00');