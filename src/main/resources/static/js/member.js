/**
 * 회원가입 폼 유효성 검사
 */
function checkJoin() {
    let id = document.getElementById('memberId');
    let pw = document.getElementById('memberPw');
    let name = document.getElementById('name');

    // 아이디 길이 검사 (3~10자)
    if (id.value.length < 3 || id.value.length > 10) {
        alert('아이디는 3~10자 사이로 입력해주세요.');
        id.focus();
        return false;
    }

    // 비밀번호 길이 검사 (3~10자)
    if (pw.value.length < 3 || pw.value.length > 10) {
        alert('비밀번호는 3~10자 사이로 입력해주세요.');
        pw.focus();
        return false;
    }

    // 이름 입력 확인
    if (name.value.trim() === '') {
        alert('이름을 입력해주세요.');
        name.focus();
        return false;
    }

    return true;
}