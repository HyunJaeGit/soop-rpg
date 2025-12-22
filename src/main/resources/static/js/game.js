/**
 * 주식 매수 요청 함수
 */
function buyStock(streamerId) {
    // 서버의 /buy 주소로 POST 요청 전송
    fetch('/buy?streamerId=' + streamerId, {
        method: 'POST'
    })
    .then(response => response.text())
    .then(data => {
        if(data === 'success') {
            alert('성공적으로 매수 요청을 보냈습니다! (ID: ' + streamerId + ')');
            // 콘솔에서 네트워크 응답 확인 가능
            console.log("매수 완료 ID:", streamerId);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('매수 요청 중 오류가 발생했습니다.');
    });
}