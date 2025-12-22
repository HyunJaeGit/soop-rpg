/**
 * 총 자산을 계산하고 등급/아바타를 한 번에 업데이트
 */
function refreshUserRank() {
    // 1. 현금 잔고 가져오기
    const balance = parseInt(document.getElementById('wallet-balance').innerText.replace(/[^0-9]/g, '')) || 0;

    // 2. 보유 주식 가치 합산 (포트폴리오 테이블의 각 행을 탐색)
    let stockValue = 0;
    const portfolioRows = document.querySelectorAll('#portfolio-table-body tr'); // 포트폴리오 테이블 ID 가정

    portfolioRows.forEach(row => {
        // 각 행에서 수량과 현재가를 찾아 곱함 (HTML 구조에 따라 선택자 수정 필요)
        const quantity = parseInt(row.querySelector('.quantity').innerText) || 0;
        const currentPrice = parseInt(row.querySelector('.current-price').innerText.replace(/[^0-9]/g, '')) || 0;
        stockValue += (quantity * currentPrice);
    });

    const totalAssets = balance + stockValue;

    // 3. 등급 판별
    let rankLevel = 1;
    let rankName = "건빵";

    if (totalAssets >= 1000000000) { rankLevel = 5; rankName = "큰손"; }
    else if (totalAssets >= 100000000) { rankLevel = 4; rankName = "열혈"; }
    else if (totalAssets >= 10000000) { rankLevel = 3; rankName = "구독자"; }
    else if (totalAssets >= 2000000) { rankLevel = 2; rankName = "팬클럽"; }

    // 4. UI 업데이트 (아바타 및 텍스트)
    const avatarImg = document.getElementById('user-avatar');
    const rankText = document.getElementById('user-rank-badge');

    if (avatarImg) avatarImg.src = `/images/avatars/rank_${rankLevel}.png`;
    if (rankText) rankText.innerText = rankName;

    console.log(`현재 총 자산: ${totalAssets}G, 등급: ${rankName}`);
}

/**
 * 매수 성공 시 호출 부분 수정
 */
function buyStock(streamerId) {
    fetch('/buy?streamerId=' + streamerId, { method: 'POST' })
    .then(response => response.text())
    .then(data => {
        if(data === 'success') {
            alert('매수 완료!');
            // 여기서 화면의 숫자를 업데이트하는 로직이 필요하거나
            // 가장 쉬운 방법은 location.reload(); 로 데이터를 새로 받아오는 것입니다.
            location.reload();
        }
    });
}

.user-status-bar {
            position: sticky;
            top: 0;
            z-index: 1000;
            background-color: rgba(255, 255, 255, 0.95);
            border-bottom: 2px solid #00c73c;
            padding: 10px 20px;
            display: flex;
            align-items: center;
            gap: 15px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .avatar-img {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            border: 2px solid #eee;
            background-color: #f0f0f0;
            object-fit: cover;
        }
        .user-info {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }
        .user-top-row {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .asset-display {
            font-size: 1.1em;
            font-weight: bold;
            color: #333;
        }
        .main-content {
            padding: 20px;
        }