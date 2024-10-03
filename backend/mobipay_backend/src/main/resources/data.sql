-- bank
INSERT ignore INTO bank (bank_code, bank_name)
VALUES ('001', '한국은행'),
       ('002', '산업은행'),
       ('003', '기업은행'),
       ('004', '국민은행'),
       ('011', '농협은행'),
       ('020', '우리은행'),
       ('023', 'SC제일은행'),
       ('027', '시티은행'),
       ('032', '대구은행'),
       ('034', '광주은행'),
       ('035', '제주은행'),
       ('037', '전북은행'),
       ('039', '경남은행'),
       ('045', '새마을금고'),
       ('081', 'KEB하나은행'),
       ('088', '신한은행'),
       ('090', '카카오뱅크'),
       ('999', '싸피은행');
-- account product
INSERT ignore INTO account_product (account_type_unique_no, account_name, bank_code)
VALUES ('001-1-5574949722ff43', '한국은행 수시입출금 상품', '001'),
       ('004-1-c880da59551a4e', '국민은행 수시입출금 상품', '004'),
       ('090-1-68131944bcc749', '카카오뱅크 수시입출금 상품', '090');
-- card issuer
INSERT ignore INTO card_issuer (card_issuer_code, card_issuer_name)
VALUES ('1001', 'KB국민카드'),
       ('1002', '삼성카드'),
       ('1003', '롯데카드'),
       ('1004', '우리카드'),
       ('1005', '신한카드'),
       ('1006', '현대카드'),
       ('1007', 'BC 바로카드'),
       ('1008', 'NH농협카드'),
       ('1009', '하나카드'),
       ('1010', 'IBK기업은행');
-- card product
INSERT ignore INTO card_product (card_unique_no, card_name, baseline_performance, max_benefit_limit, card_description,
                                 card_issuer_code)
VALUES ('1001-664f125022bf433', '슬기로운 국민카드', '700000', '130000', '생활20%할인, 교통10% 할인, 대형마트5% 할인', '1001'),
       ('1002-218c5933582e430', '삼성카드 taptap O', '700000', '130000', '통신10%할인, 교통10% 할인, 대형마트5% 할인', '1002'),
       ('1005-6d3da5e1ab334fc', '신한카드 SOL트래블', '700000', '130000', '주유7%할인, 교통10% 할인, 대형마트5% 할인', '1005'),
       ('1005-dcf45d7885a1442', '하나카드 MULTI Oil', '200000', '1000000', '주유 10%할인, 생활 5% 할인', '1005');
-- merchant
INSERT ignore INTO merchant (merchant_id, category_id, merchant_name, lat, lng, mobi_api_key)
VALUES (1906, 'CG-9ca85f66311a23d', '진평주차장', '36.108690', '128.419555', '9dX2hN4jLwT7vK8pYmQ5'),
       (1907, 'CG-3fa85f6425e811e', '인동주유소', '36.110484', '128.419550', 'zF7R3jN1pV8aG6tLxB0'),
       (1908, 'CG-9ca85f66311a23d', '구미셀프세차장', '36.110290', '128.422617', 'G3kP9tX5hJ8nL4mQ7aV'),
       (1909, 'CG-9ca85f66311a23d', '노상주차장', '36.107011', '128.421024', 'R2pX8vN7gL4qJ5yK1wT'),
       (1910, 'CG-9ca85f66311a23d', '투스데이', '36.108661', '128.419280', 'D4wZ6nT2vK3xR9yP8jM'),
       (1911, 'CG-9ca85f66311a23d', '스타벅스 구미인의DT점', '36.095567', '128.431260', 'K1qT4xM9jW2bF5vYcN7');
--     ('CG-9ca85f66311a23d', '스타벅스', 'lat', 'lng', 'K1qT4xM9jW2bF5vYcN7'),
--     ('CG-3fa85f6425e811e', '현대오일뱅크', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-3fa85f6425e811e', 'SK엔크린', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-3fa85f6425e811e', 'GS칼텍스', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-4fa85f6455cad4a', '코레일', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-4fa85f6455cad4a', '한국철도공사', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-4fa85f6425ad1d3', '이마트 은평점', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-4fa85f6425ad1d3', '롯데마트 강변점', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-4fa85f6425ad1d3', '코스트코', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-4fa85f6455cad4a', '카카오택시', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-6dd85f6425ez11o', '타요키즈카페 대전둔산점', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-6dd85f6425ez11o', '메가스터디교육(주)', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-6dd85f6425ez11o', '가온누리 어린이집', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-7fa85f6425bc311', 'SK텔레콤', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-7fa85f6425bc311', '(주)KT', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-7fa85f6425bc311', '엘지유플러스', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-8fa85f6425e1123', '알리익스프레스', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-8fa85f6425e1123', '아마존 코리아', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-8fa85f6425e1123', 'Temu', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-9ca85f66311a23d', '스타벅스 서울역점', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-9ca85f66311a23d', '버거킹 신촌점', 'lat', 'lng', 'mobi_api_key'),
--     ('CG-9ca85f66311a23d', '24시 아이스크림 할인점', 'lat', 'lng', 'mobi_api_key'),

COMMIT;