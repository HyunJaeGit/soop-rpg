/**
 * 상세 페이지 전용 로직 (그래프 및 거래)
 */

// 1. 그래프 초기화 함수
function initChart(historyData) {
    if (!historyData || historyData.length === 0) return;

    const prices = historyData.map(h => h.price).reverse();
    const times = historyData.map(h => {
        const date = new Date(h.recordedAt);
        return `${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`;
    }).reverse();

    const options = {
        chart: { type: 'line', height: 250, toolbar: { show: false }, animations: { enabled: true } },
        series: [{ name: '주가', data: prices }],
        xaxis: { categories: times },
        stroke: { curve: 'smooth', width: 3 },
        colors: ['#007bff'],
        tooltip: { y: { formatter: (val) => val.toLocaleString() + " G" } }
    };

    const chart = new ApexCharts(document.querySelector("#chart"), options);
    chart.render();
}

// 2. 거래 실행 함수 (매수/매도 공용)
function trade(type, streamerId) {
    const qtyInput = (type === 'buy') ? document.getElementById('buyQuantity') : document.getElementById('sellQuantity');
    const qty = qtyInput.value;
    const url = (type === 'buy') ? '/buy' : '/sell';

    if (qty <= 0) {
        alert("1주 이상 입력해야 합니다.");
        return;
    }

    if (!confirm(`${qty}주를 ${type === 'buy' ? '매수' : '매도'}하시겠습니까?`)) return;

    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `streamerId=${streamerId}&quantity=${qty}`
    })
    .then(res => res.text())
    .then(data => {
        if (data === 'success') {
            alert(`${qty}주 ${type === 'buy' ? '매수' : '매도'} 성공!`);
            location.href = '/';
        } else {
            alert("거래 실패: 잔액이 부족하거나 보유 수량이 부족합니다.");
        }
    })
    .catch(err => console.error("Error:", err));
}

// 3. 실시간 금액 계산기
function initPriceCalculator(currentPrice) {
    const buyInput = document.getElementById('buyQuantity');
    const display = document.getElementById('totalPrice');

    if(buyInput && display) {
        buyInput.addEventListener('input', (e) => {
            display.innerText = (e.target.value * currentPrice).toLocaleString();
        });
    }
}