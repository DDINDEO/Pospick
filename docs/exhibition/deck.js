// POSPICK 최종 발표 PPT (16:9) — 판넬과 같은 톤: 남색 + 포인트 1색, 글 위주, 사진 자리 표시
const pptxgen = require("pptxgenjs");
const OUT = process.argv[2] || "POSPICK_발표.pptx";

const P = {
  accent: "4F46E5", navy: "1E293B", photo: "EEF0F3", line: "D9DCE1",
  text: "1F2937", sub: "4B5563", muted: "8A919C", white: "FFFFFF",
};
const FONT = "Malgun Gothic";
const W = 13.333, H = 7.5, M = 0.7;

const pres = new pptxgen();
pres.layout = "LAYOUT_WIDE";
pres.theme = { headFontFace: FONT, bodyFontFace: FONT };
pres.title = "POSPICK 최종 발표";
pres.author = "김진서";

pres.defineSlideMaster({
  title: "CONTENT",
  background: { color: P.white },
  objects: [
    { text: { text: "POSPICK", options: { x: M, y: H - 0.5, w: 3, h: 0.3, fontFace: FONT, fontSize: 10, bold: true, color: P.muted, margin: 0 } } },
  ],
  slideNumber: { x: W - M - 0.6, y: H - 0.5, w: 0.6, h: 0.3, fontFace: FONT, fontSize: 10, color: P.muted, align: "right" },
});

const T = (s, text, o) => s.addText(text, { isTextBox: true, fontFace: FONT, margin: 0, valign: "top", color: P.text, ...o });

// 사진 넣을 자리: 연회색 영역 + 안내 문구
const photo = (s, x, y, w, h, label) => {
  s.addShape(pres.shapes.RECTANGLE, { x, y, w, h, fill: { color: P.photo }, line: { type: "none" }, objectName: `photo: ${label}` });
  T(s, [
    { text: "사진", options: { fontSize: 12, bold: true, color: P.accent, breakLine: true } },
    { text: label, options: { fontSize: 14, color: P.muted } },
  ], { x, y, w, h, align: "center", valign: "middle" });
};

// 공통 제목: 작은 섹션 번호 + 제목
const content = (kicker, title, notes) => {
  const s = pres.addSlide({ masterName: "CONTENT" });
  T(s, kicker, { x: M, y: 0.5, w: 8, h: 0.35, fontSize: 14, bold: true, color: P.accent });
  T(s, title, { x: M, y: 0.85, w: W - 2 * M, h: 0.75, fontSize: 32, bold: true, color: P.navy });
  if (notes) s.addNotes(notes);
  return s;
};

// 소제목 + 설명 묶음 (글로만)
const item = (s, x, y, w, head, body, hs = 20, bs = 15) => {
  T(s, head, { x, y, w, h: 0.45, fontSize: hs, bold: true, color: P.navy });
  T(s, body, { x, y: y + 0.5, w, h: 0.9, fontSize: bs, color: P.sub, lineSpacingMultiple: 1.2 });
};
const bullets = (s, x, y, w, h, items, fs = 16) =>
  T(s, items.map((t, i) => ({ text: t, options: { bullet: { indent: 16 }, breakLine: i < items.length - 1 } })),
    { x, y, w, h, fontSize: fs, color: P.text, paraSpaceAfter: 8 });

// ---------- 1. 표지 ----------
{
  const s = pres.addSlide({ masterName: "CONTENT" });
  T(s, "컴퓨터SW 졸업작품 최종 발표", { x: M, y: 1.2, w: 6, h: 0.4, fontSize: 16, color: P.sub });
  T(s, [{ text: "POS", options: { color: P.navy } }, { text: "PICK", options: { color: P.accent } }],
    { x: M, y: 1.8, w: 6.2, h: 1.4, fontSize: 80, bold: true });
  T(s, "행사 부스 판매자를 위한 웹 POS 시스템", { x: M, y: 3.35, w: 6.2, h: 0.6, fontSize: 24, bold: true, color: P.navy });
  T(s, "카드 단말기 없이, URL 하나로 시작하는 현장 판매", { x: M, y: 4.0, w: 6.2, h: 0.4, fontSize: 14, color: P.sub });
  T(s, "컴퓨터SW 21017019  김진서", { x: M, y: 5.6, w: 6, h: 0.4, fontSize: 16, bold: true, color: P.navy });
  photo(s, 7.4, 1.0, W - 7.4 - M, 5.4, "메인 화면 캡처");
}

// ---------- 2. 목차 ----------
{
  const s = content("CONTENTS", "목차");
  const toc = ["프로젝트 배경", "문제 정의", "기존 서비스와 차별점", "시스템 설계", "주요 기능", "기술적 고민", "시연", "결론 및 향후 계획"];
  toc.forEach((t, i) => {
    const col = Math.floor(i / 4), row = i % 4;
    const x = M + col * 5.8, y = 2.2 + row * 1.05;
    T(s, String(i + 1).padStart(2, "0"), { x, y, w: 0.9, h: 0.6, fontSize: 28, bold: true, color: P.accent });
    T(s, t, { x: x + 1.0, y: y + 0.05, w: 4.5, h: 0.6, fontSize: 24, bold: true, color: P.navy });
  });
}

// ---------- 3. 프로젝트 배경 ----------
{
  const s = content("01  프로젝트 배경", "현금과 계좌이체로 움직이는 행사 부스");
  T(s, "코믹월드 같은 동인 행사, 플리마켓, 대학 축제에서 부스를 운영하는 개인 판매자는 대부분 현금과 계좌이체로 거래합니다.",
    { x: M, y: 2.0, w: 5.4, h: 1.3, fontSize: 17, color: P.text, lineSpacingMultiple: 1.3 });
  T(s, "하지만 기존 모바일 POS 앱은 카드 단말기 · 사업자등록 · 월 구독료를 전제로 설계되어 있어 개인 판매자에게 맞지 않습니다.",
    { x: M, y: 3.5, w: 5.4, h: 1.3, fontSize: 17, color: P.text, lineSpacingMultiple: 1.3 });
  photo(s, 6.6, 1.9, 2.9, 4.6, "행사 현장 사진");
  photo(s, 9.7, 1.9, W - 9.7 - M, 4.6, "부스 운영 모습");
}

// ---------- 4. 문제 정의 ----------
{
  const s = content("02  문제 정의", "현장에서 반복되는 네 가지 문제");
  const probs = [
    ["암산 · 거스름돈 실수", "손님이 몰리는 시간에 합계와 거스름돈을\n머릿속으로 계산하다 생기는 오류"],
    ["수기 매출 정리", "행사가 끝난 뒤 판매 내역을\n노트나 메모장에서 다시 정리하는 번거로움"],
    ["재고 파악 어려움", "무엇이 얼마나 남았는지 몰라\n품절 안내가 늦거나 재고가 남는 상황"],
    ["주최측 데이터 부재", "주최측은 어떤 부스와 품목이 잘 팔렸는지\n알 수 없어 다음 행사 기획이 어려움"],
  ];
  probs.forEach(([h, b], i) => {
    const x = M + (i % 2) * 6.2, y = 2.1 + Math.floor(i / 2) * 2.2;
    T(s, String(i + 1).padStart(2, "0"), { x, y, w: 0.8, h: 0.5, fontSize: 22, bold: true, color: P.accent });
    item(s, x + 0.85, y, 5.0, h, b, 22, 16);
  });
}

// ---------- 5. 차별점 ----------
{
  const s = content("03  기존 서비스와 차별점", "개인 판매자에게 맞춘 POS");
  const head = (t, c) => ({ text: t, options: { bold: true, color: c || P.navy, border: [{ type: "none" }, { type: "none" }, { pt: 1.5, color: P.navy }, { type: "none" }] } });
  const cell = (t, o = {}) => ({ text: t, options: { border: [{ type: "none" }, { type: "none" }, { pt: 0.75, color: P.line }, { type: "none" }], ...o } });
  const rows = [
    [head("항목"), head("기존 POS 앱", P.muted), head("POSPICK", P.accent)],
    ...[
      ["사업자등록", "필수", "불필요"],
      ["카드 단말기", "필수", "불필요"],
      ["행사 단위 운영", "없음", "행사별 데이터 분리"],
      ["상품 UI", "텍스트 리스트", "이미지 그리드"],
      ["월 구독료", "월 1~5만 원", "무료"],
      ["설치", "앱스토어 다운로드", "URL 접속만으로 실행"],
      ["지원 환경", "Android 위주", "iOS · Android · PC"],
    ].map(([a, b, c]) => [cell(a, { bold: true }), cell(b, { color: P.muted }), cell(c, { bold: true, color: P.navy })]),
  ];
  s.addTable(rows, { x: M, y: 1.95, w: W - 2 * M, colW: [3.2, 4.3, W - 2 * M - 7.5], rowH: 0.56, fontFace: FONT, fontSize: 16, valign: "middle", margin: [0, 0.1, 0, 0] });
}

// ---------- 6. 시스템 구조 ----------
{
  const s = content("04  시스템 설계", "시스템 구조 및 기술 스택");
  const stack = [
    ["Frontend", "React 19 · Vite · React Router\nChart.js · SheetJS"],
    ["Backend", "Java · Spring Boot 4\nSpring Security · JWT · JPA"],
    ["Database", "MySQL"],
    ["Cloud · AI", "AWS S3 · Google Gemini API"],
  ];
  stack.forEach(([h, b], i) => {
    const y = 2.0 + i * 1.12;
    T(s, h, { x: M, y, w: 4.2, h: 0.4, fontSize: 18, bold: true, color: P.navy });
    T(s, b, { x: M, y: y + 0.4, w: 4.2, h: 0.65, fontSize: 14, color: P.sub, lineSpacingMultiple: 1.15 });
  });
  photo(s, 5.3, 1.9, W - 5.3 - M, 4.7, "시스템 구조도 (README 아키텍처 이미지)");
}

// ---------- 7. 사용 흐름 ----------
{
  const s = content("04  시스템 설계", "서비스 사용 흐름");
  const flow = [
    ["주최자", "행사 생성", "수집 옵션 설정"],
    ["판매자", "참가 신청", "데이터 수집 동의"],
    ["주최자", "승인 / 거절", "신청 목록 관리"],
    ["판매자", "상품 등록", "S3 업로드 · AI 분류"],
    ["판매자", "POS 판매", "재고 자동 차감"],
    ["공통", "통계 · 정산", "차트 · 엑셀 추출"],
  ];
  const fw = (W - 2 * M) / 6;
  flow.forEach(([who, t, d], i) => {
    const x = M + i * fw;
    T(s, [{ text: String(i + 1).padStart(2, "0"), options: { fontSize: 16, bold: true, color: P.accent } },
          { text: "  " + who, options: { fontSize: 11, color: P.muted } }], { x, y: 1.95, w: fw - 0.1, h: 0.35, valign: "bottom" });
    T(s, t, { x, y: 2.35, w: fw - 0.1, h: 0.4, fontSize: 17, bold: true, color: P.navy });
    T(s, d, { x, y: 2.75, w: fw - 0.1, h: 0.35, fontSize: 12, color: P.sub });
  });
  photo(s, M, 3.4, W - 2 * M, 3.3, "사용 흐름도 (README 이미지)");
}

// ---------- 8. ERD ----------
{
  const s = content("04  시스템 설계", "데이터베이스 설계 (ERD)");
  T(s, "User · Event · Participation · Product · Order · OrderItem 6개 엔티티로 행사 단위 데이터를 분리해 저장합니다.",
    { x: M, y: 1.75, w: W - 2 * M, h: 0.4, fontSize: 15, color: P.sub });
  photo(s, M, 2.35, W - 2 * M, 4.35, "ERD 이미지");
}

// ---------- 9. 행사 · 참가 관리 ----------
{
  const s = content("05  주요 기능", "행사 및 참가 관리");
  item(s, M, 1.95, 4.0, "주최자", "행사 생성 · 삭제\n매출 · 재고 수집 옵션 설정\n참가 신청 승인 / 거절", 20, 15);
  item(s, M, 3.85, 4.0, "판매자", "행사 검색 후 참가 신청\n주최측 수집 옵션에 동의해야 신청 가능\n동의 시각(agreedAt) 기록", 20, 15);
  const pw = (W - M - 5.0 - 0.3) / 2;
  photo(s, 5.0, 1.9, pw, 4.8, "행사 생성 화면");
  photo(s, 5.0 + pw + 0.3, 1.9, pw, 4.8, "참가 신청 승인 화면");
}

// ---------- 10. 상품 등록 + AI ----------
{
  const s = content("05  주요 기능", "상품 등록과 AI 자동 분류");
  bullets(s, M, 2.0, 4.0, 3.5, [
    "이름 · 가격 · 재고 · 이미지로 상품 등록",
    "이미지는 AWS S3에 업로드",
    "Gemini가 카테고리 · 신뢰도 반환",
    "분류할 수 없으면 '기타'로 처리",
    "재고가 적으면 매진 임박 뱃지 표시",
  ], 15);
  const pw = (W - M - 5.0 - 0.3) / 2;
  photo(s, 5.0, 1.9, pw, 4.8, "상품 등록 화면");
  photo(s, 5.0 + pw + 0.3, 1.9, pw, 4.8, "AI 분류 결과 화면");
}

// ---------- 11. POS 결제 ----------
{
  const s = content("05  주요 기능", "POS 결제");
  bullets(s, M, 2.0, 3.6, 3.5, [
    "상품 이미지를 눌러 선택",
    "합계 자동 계산",
    "현금: 거스름돈 자동 계산",
    "계좌이체: 바로 결제 완료",
    "결제 즉시 재고 차감",
    "주문 취소 시 재고 복구",
  ], 15);
  photo(s, 4.6, 1.9, W - 4.6 - M, 4.8, "POS 결제 화면 (상품 그리드 + 장바구니)");
}

// ---------- 12. 통계 대시보드 ----------
{
  const s = content("05  주요 기능", "통계 대시보드");
  bullets(s, M, 2.0, 3.6, 3.5, [
    "결제 수단별 매출",
    "시간대별 판매 피크",
    "상품별 재고 소진율",
    "Chart.js 차트 (Bar · Line · Pie)",
    "SheetJS로 엑셀 내보내기",
  ], 15);
  const pw = (W - M - 4.6 - 0.3) / 2;
  photo(s, 4.6, 1.9, pw, 4.8, "판매자 통계 화면");
  photo(s, 4.6 + pw + 0.3, 1.9, pw, 4.8, "주최자 통계 화면");
}

// ---------- 13. 기술적 고민 ----------
{
  const s = content("06  기술적 고민", "구현하면서 고민한 부분");
  const items = [
    ["재고 정합성", "주문 생성 시 재고를 먼저 확인하고 부족하면 거절합니다. 재고 차감과 주문 저장을 하나의 트랜잭션으로 묶고, 주문 취소 시 같은 방식으로 재고를 복구합니다."],
    ["동의 기반 데이터 공유", "주최측이 수집 옵션을 켠 행사는 판매자가 동의해야 참가 신청할 수 있습니다. 동의 여부와 시각을 기록해 데이터 열람 권한의 근거로 사용합니다."],
    ["AI 분류 결과 처리", "Gemini가 메인 · 서브 카테고리와 신뢰도를 JSON으로 답하도록 프롬프트를 설계했습니다. 응답이 비정상이면 '기타'로 대체해 상품 등록이 막히지 않게 했습니다."],
  ];
  const cw = (W - 2 * M - 0.8) / 3;
  items.forEach(([h, b], i) => {
    const x = M + i * (cw + 0.4);
    T(s, String(i + 1).padStart(2, "0"), { x, y: 2.1, w: 1, h: 0.5, fontSize: 24, bold: true, color: P.accent });
    T(s, h, { x, y: 2.65, w: cw, h: 0.5, fontSize: 20, bold: true, color: P.navy });
    T(s, b, { x, y: 3.25, w: cw, h: 2.0, fontSize: 14, color: P.sub, lineSpacingMultiple: 1.35 });
  });
  s.addNotes("실제로 겪은 문제와 해결 과정으로 바꿔서 이야기하면 좋음");
}

// ---------- 14. 시연 ----------
{
  const s = content("07  시연", "시연");
  photo(s, M, 1.85, W - 2 * M, 4.85, "시연 영상 (행사 생성 → 참가 → 상품 등록 → 결제 → 통계)");
}

// ---------- 15. 결론 ----------
{
  const s = content("08  결론 및 향후 계획", "결론 및 향후 계획");
  T(s, "결과", { x: M, y: 2.0, w: 5.6, h: 0.45, fontSize: 22, bold: true, color: P.navy });
  bullets(s, M, 2.6, 5.6, 3.5, [
    "카드 단말기 없이 현금 · 계좌이체 판매를 기록하는 웹 POS 구현",
    "결제와 동시에 재고 차감, 행사 단위 매출 통계 자동화",
    "주최자와 판매자 간 동의 기반 데이터 공유 구조",
  ], 16);
  T(s, "향후 계획", { x: 7.0, y: 2.0, w: 5.6, h: 0.45, fontSize: 22, bold: true, color: P.navy });
  bullets(s, 7.0, 2.6, W - 7.0 - M, 3.5, [
    "PWA 적용으로 행사장 와이파이 불안정 시 오프라인 동작",
    "외부 서버 배포",
    "PDF 리포트 추출",
    "비정상 재고 변동 · 매출 흐름 감지",
  ], 16);
}

// ---------- 16. 마무리 ----------
{
  const s = pres.addSlide({ masterName: "CONTENT" });
  T(s, "감사합니다", { x: M, y: 2.6, w: W - 2 * M, h: 1.1, fontSize: 54, bold: true, color: P.navy, align: "center" });
  T(s, "Q & A", { x: M, y: 3.8, w: W - 2 * M, h: 0.6, fontSize: 24, color: P.accent, bold: true, align: "center" });
}

pres.writeFile({ fileName: OUT }).then(() => console.log("wrote", OUT));
