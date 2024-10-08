const MER_API_KEY = 'Da19J03F6g7H8iB2c54e';

// 거래내역 조회
const t_modal = document.querySelector('#getTransactions');

t_modal.addEventListener('click', async () => {
    const url = `/api/v1/merchants/${t_modal.getAttribute('merchantType')}/transactions`;
    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'merApiKey': MER_API_KEY,
            },
        });

        if(response?.status !== 200) {
            alert('거래내역 조회에 실패했습니다.');
            console.error('Error:', response);  
            return;
        }
    } catch (error) {
        console.error('Error:', error);
        return;
    }
    const data = await response.json();
    console.log(data);
});

// 거래 취소
