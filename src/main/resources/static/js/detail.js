/**
 * [함수 역할]: 서버에서 받은 히스토리 데이터를 사용하여 ApexCharts 그래프를 초기화합니다.
 */
function initChart(historyData) {
    if (!historyData || historyData.length === 0) {
        const chartElement = document.querySelector("#chart");
        if(chartElement) chartElement.innerHTML = "<p style='padding:20px; color:#999;'>아직 충분한 데이터가 쌓이지 않았습니다. (1분만 기다려주세요!)</p>";
        return;
    }

    // 최신 데이터가 오른쪽으로 오도록 정렬 (기존 데이터가 Descending일 경우 대비)
    const sortedData = [...historyData].sort((a, b) => new Date(a.recordedAt) - new Date(b.recordedAt));

    const prices = sortedData.map(item => item.price);
    const times = sortedData.map(item => {
        const date = new Date(item.recordedAt);
        return date.getHours() + ":" + String(date.getMinutes()).padStart(2, '0');
    });

    const options = {
        series: [{ name: '주가', data: prices }],
        chart: { type: 'area', height: 300, zoom: { enabled: false }, toolbar: { show: false } },
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 3 },
        xaxis: { categories: times },
        yaxis: { labels: { formatter: (val) => Math.floor(val).toLocaleString() + " G" } },
        colors: ['#007bff'],
        fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.7, opacityTo: 0.3 } }
    };

    const chart = new ApexCharts(document.querySelector("#chart"), options);
    chart.render();
}

/**
 * [함수 역할]: 매수 수량을 입력할 때 총 금액을 자동으로 계산해줍니다.
 */
function initPriceCalculator(currentPrice) {
    const buyInput = document.getElementById('buyQuantity');
    const totalPriceDisplay = document.getElementById('totalPrice');

    if(buyInput && totalPriceDisplay) {
        buyInput.addEventListener('input', (e) => {
            const quantity = parseInt(e.target.value) || 0;
            totalPriceDisplay.innerText = (quantity * currentPrice).toLocaleString();
        });
    }
}

/**
 * [함수 역할]: 매수/매도 버튼을 눌렀을 때 서버의 TradeController와 통신합니다.
 */
function trade(type, streamerId) {
    const quantityInput = document.getElementById(type === 'buy' ? 'buyQuantity' : 'sellQuantity');
    const quantity = parseInt(quantityInput.value);

    if (isNaN(quantity) || quantity <= 0) {
        alert("올바른 수량을 입력해주세요.");
        return;
    }

    // 서버 API 호출
    fetch('/api/trade', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            type: type,
            id: streamerId,
            quantity: quantity
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            location.reload(); // 성공 시 페이지 새로고침하여 잔액/포트폴리오 갱신
        } else {
            alert("거래 실패: " + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert("서버 통신 중 에러가 발생했습니다.");
    });
}