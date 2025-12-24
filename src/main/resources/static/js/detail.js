/**
 * [함수 역할]: 서버에서 받은 히스토리 데이터를 사용하여 ApexCharts 그래프를 초기화합니다.
 */
function initChart(historyData) {
    if (!historyData || historyData.length === 0) {
        document.querySelector("#chart").innerHTML = "<p style='padding:20px; color:#999;'>아직 충분한 데이터가 쌓이지 않았습니다. (1분만 기다려주세요!)</p>";
        return;
    }

    // 1. 그래프에 표시할 가격과 시간 데이터를 추출합니다.
    const prices = historyData.map(item => item.price);
    const times = historyData.map(item => {
        const date = new Date(item.recordedAt);
        return date.getHours() + ":" + date.getMinutes(); // "14:30" 형태로 표시
    });

    // 2. ApexCharts 설정 옵션
    const options = {
        series: [{
            name: '주가',
            data: prices
        }],
        chart: {
            type: 'area', // 영역 차트로 더 예쁘게 표시
            height: 300,
            zoom: { enabled: false },
            toolbar: { show: false }
        },
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 3 }, // 부드러운 곡선
        xaxis: { categories: times },
        yaxis: { labels: { formatter: (val) => val.toLocaleString() + " G" } },
        colors: ['#007bff'], // 메인 테마색
        fill: {
            type: 'gradient',
            gradient: { shadeIntensity: 1, opacityFrom: 0.7, opacityTo: 0.9 }
        }
    };

    // 3. 차트 렌더링
    const chart = new ApexCharts(document.querySelector("#chart"), options);
    chart.render();
}

/**
 * [함수 역할]: 매수 수량을 입력할 때 총 금액을 자동으로 계산해줍니다.
 */
function initPriceCalculator(currentPrice) {
    const buyInput = document.getElementById('buyQuantity');
    const sellInput = document.getElementById('sellQuantity');
    const totalPriceDisplay = document.getElementById('totalPrice');

    function updatePrice(e) {
        const quantity = e.target.value;
        totalPriceDisplay.innerText = (quantity * currentPrice).toLocaleString();
    }

    if(buyInput) buyInput.addEventListener('input', updatePrice);
    if(sellInput) sellInput.addEventListener('input', updatePrice);
}

/**
 * [함수 역할]: 매수/매도 버튼을 눌렀을 때 실행될 로직 (추후 3단계에서 서버와 연동)
 */
function trade(type, streamerId) {
    const quantity = (type === 'buy')
        ? document.getElementById('buyQuantity').value
        : document.getElementById('sellQuantity').value;

    alert(type + " 요청: " + streamerId + "번 스트리머 주식 " + quantity + "주\n(거래 시스템은 3단계에서 구현됩니다!)");
}