$(document).ready(function() {
    // 1. 게시판 목록 페이지인 경우 초기 로딩 및 이벤트 설정
    if ($('#listArea').length > 0) {
        loadList('ALL');

        $('#categorySelect').change(function() {
            loadList($(this).val());
        });
    }

    // 2. 신청 결과 메시지 확인 (상세 페이지)
    // URL 파라미터나 hidden input 등으로 에러 체크가 필요하다면 여기서 구현
});

/**
 * 게시판 목록 Ajax 로딩
 */
function loadList(category) {
    $.ajax({
        url: '/board/ajaxList',
        type: 'post',
        data: { category: category },
        success: function(list) {
            let html = '';
            $.each(list, function(i, item) {
                let statusClass = (item.status === 'OPEN') ? 'blue' : 'red';

                html += `<tr>
                    <td>${item.category}</td>
                    <td><a href="/board/detail?boardNum=${item.boardNum}">${item.title}</a></td>
                    <td>${item.memberId}</td>
                    <td class="${statusClass}">${item.status}</td>
                    <td>(${item.headcnt}/${item.capacity})</td>
                    <td>${item.createDate}</td>
                </tr>`;
            });
            $('#listArea').html(html);
        },
        error: function() {
            alert('목록을 불러오는데 실패했습니다.');
        }
    });
}

/**
 * 게시글 삭제 확인
 */
function checkDelete(boardNum) {
    if (confirm('정말 삭제하시겠습니까?')) {
        location.href = '/board/delete?boardNum=' + boardNum;
    }
}

/**
 * 모임 신청 확인
 */
function checkApply(boardNum) {
    if (confirm('모임에 신청하시겠습니까?')) {
        location.href = '/board/apply?boardNum=' + boardNum;
    }
}

/**
 * 승인/거절 처리 확인
 */
function processApplication(groupId, action, boardNum) {
    let msg = (action === 'APPROVE') ? '승인하시겠습니까?' : '거절하시겠습니까?';
    if (confirm(msg)) {
        location.href = '/board/process?groupId=' + groupId + '&action=' + action + '&boardNum=' + boardNum;
    }
}
/**
 * 글쓰기 폼 유효성 검사
 */
function checkWrite() {
    let title = document.getElementById('title');
    let contents = document.getElementById('contents');
    let capacity = document.getElementById('capacity');

    // 제목 길이 검사
    if (title.value.trim().length < 3 || title.value.trim().length > 100) {
        alert('제목은 3~100자 사이로 입력해주세요.');
        title.focus();
        return false;
    }

    // 카테고리가 선택되었는지 확인
    let category = document.getElementById('category');
    if (category.value === '') {
        alert('카테고리를 선택해주세요.');
        category.focus();
        return false;
    }

    // 내용 입력 확인
    if (contents.value.trim() === '') {
        alert('내용을 입력해주세요.');
        contents.focus();
        return false;
    }

    // 정원 검사 (2명 이상)
    if (capacity.value < 2) {
        alert('정원은 최소 2명 이상이어야 합니다.');
        capacity.focus();
        return false;
    }

    return true;
}