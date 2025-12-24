/**
 * 총 자산을 계산하고 등급/아바타를 한 번에 업데이트
 */
function refreshUserRank() {
    // 1. 현금 잔고 가져오기 (지갑 텍스트에서 콤마 제거 후 숫자로 변환)
    const balanceElement = document.getElementById('wallet-balance');
    if (!balanceElement) return;
    const balance = parseInt(balanceElement.innerText.replace(/[^0-9]/g, '')) || 0;

    // 2. 보유 주식 가치 합산 (현재 HTML 구조인 .portfolio-list 내부 탐색)
    let stockValue = 0;
    const portfolioItems = document.querySelectorAll('.portfolio-list > div');

    portfolioItems.forEach(item => {
        // "수량: 10주" 형태에서 숫자만 추출
        const quantityText = item.querySelector('span').innerText;
        const quantity = parseInt(quantityText.replace(/[^0-9]/g, '')) || 0;

        // "평단가 1,000 G" 형태에서 숫자만 추출
        const avgPriceText = item.querySelector('div[style*="font-weight: bold"]').innerText;
        const avgPrice = parseInt(avgPriceText.replace(/[^0-9]/g, '')) || 0;

        stockValue += (quantity * avgPrice);
    });

    const totalAssets = balance + stockValue;

    // 3. 등급 판별 (귀여운 캐주얼 게임 기준 자산 설정)
    let rankLevel = 1;
    let rankName = "건빵";

    if (totalAssets >= 10000000) { rankLevel = 5; rankName = "큰손"; }
    else if (totalAssets >= 5000000) { rankLevel = 4; rankName = "열혈"; }
    else if (totalAssets >= 2000000) { rankLevel = 3; rankName = "구독자"; }
    else if (totalAssets >= 500000) { rankLevel = 2; rankName = "팬클럽"; }

    // 4. UI 업데이트
    const avatarImg = document.getElementById('user-avatar');
    const rankBadge = document.getElementById('user-rank-badge');

    // 이미지 경로 수정: /images/ 가 아닌 /image/ 일 수 있으니 확인 필요
    if (avatarImg) avatarImg.src = `/image/avatars/rank_${rankLevel}.png`;
    if (rankBadge) rankBadge.innerText = rankName;
}