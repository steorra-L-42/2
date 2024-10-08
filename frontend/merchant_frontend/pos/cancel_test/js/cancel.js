const MER_API_KEY = 'Da19J03F6g7H8iB2c54e';

// 거래내역 조회
const t_modal = document.querySelector('#getTransactions');
const t_list = document.querySelector('#transactionList');

t_modal.addEventListener('click', async () => {
    const url = `/api/v1/merchants/${t_modal.getAttribute('merchantType')}/transactions`;
    const data = await getTransactions(url);

    t_list.innerHTML = `
        <tr>
            <th>거래번호</th>
            <th>거래일자</th>
            <th>거래시간</th>
            <th>거래금액</th>
            <th>거래정보</th>
            <th>거래취소 여부</th>
            <th>거래취소</th>
        </tr>
    `;
    data?.items.forEach(item => {
       const cancelled = item.cancelled ? '취소 불가' : '취소 가능';
       t_list.innerHTML += `
        <tr>
            <td>${item.transactionUniqueNo}</td>
            <td>${item.transactionDate}</td>
            <td>${item.transactionTime}</td>
            <td>${item.paymentBalance}</td>
            <td>${item.info}</td>
            <td>${cancelled}</td>
            <td><button class="btn btn-danger" onclick="cancelTransaction(${item.transactionUniqueNo})">취소</button></td>
        </tr>
       `; 
    });
});

// get fetch
async function getTransactions(url) {
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

        const data = await response.json();
        console.log(data);
        return data

    } catch (error) {
        console.error('Error:', error);
        return;
    }
}


// 거래 취소
function cancelTransaction(transactionUniqueNo) {
    const url = `/api/v1/merchants/${t_modal.getAttribute('merchantType')}/cancelled-transactions/${transactionUniqueNo}`;
    patchTransaction(url);
}

// patch fetch
async function patchTransaction(url) {
    try {
        const response = await fetch(url, {
            method: 'PATCH',
            headers: {
                'merApiKey': MER_API_KEY,
            },
        });

        if(response?.status !== 200) {
            alert('거래 취소에 실패했습니다.');
            console.error('Error:', response);
            return;
        }   

        alert('거래가 취소되었습니다.');
        t_modal.click(); // 거래내역 갱신
    } catch (error) {
        console.error('Error:', error);
        return;
    }
}
