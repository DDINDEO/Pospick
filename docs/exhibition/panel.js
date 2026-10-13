const pptxgen = require("pptxgenjs");
const React = require("react");
const ReactDOMServer = require("react-dom/server");
const sharp = require("sharp");
const fa = require("react-icons/fa");

const OUT = process.argv[2] || "POSPICK_판넬.pptx";

// Palette — taken from the POSPICK front-end (indigo brand + navy)
const P = {
  accent: "4F46E5", navy: "1E293B", card: "F4F5F7", line: "D9DCE1",
  text: "1F2937", sub: "4B5563", muted: "6B7280", white: "FFFFFF",
};
const FONT = "Malgun Gothic";

async function icon(Comp, color, px = 256) {
  const svg = ReactDOMServer.renderToStaticMarkup(
    React.createElement(Comp, { color: "#" + color, size: px })
  );
  const buf = await sharp(Buffer.from(svg)).png().toBuffer();
  return "image/png;base64," + buf.toString("base64");
}

(async () => {
  const pres = new pptxgen();
  pres.defineLayout({ name: "A1_PORTRAIT", width: 23.386, height: 33.110 }); // 594 x 841 mm
  pres.layout = "A1_PORTRAIT";
  pres.theme = { headFontFace: FONT, bodyFontFace: FONT };
  pres.title = "POSPICK 졸업작품 판넬";
  pres.author = "김진서";

  const s = pres.addSlide();
  s.background = { color: P.white };
  const W = 23.386, M = 1.0, CW = W - 2 * M;

  const T = (text, o) => s.addText(text, { isTextBox: true, fontFace: FONT, margin: 0, valign: "top", ...o });

  // ---------- Header ----------
  T("Department of Computer Science", { x: M, y: 0.75, w: 10, h: 0.7, fontSize: 28, bold: true, color: P.navy, charSpacing: 2 });
    T("컴퓨터SW 제 ○회 졸업작품 전시회", { x: W - M - 9.2, y: 0.65, w: 9.2, h: 0.9, fontSize: 28, color: P.sub, align: "right", valign: "middle" });

  // ---------- Hero ----------
  const heroY = 1.9, heroH = 5.0;
  s.addShape(pres.shapes.ROUNDED_RECTANGLE, { x: M, y: heroY, w: CW, h: heroH, rectRadius: 0.4, fill: { color: P.card }, line: { type: "none" } });
  T([
    { text: "POS", options: { color: P.navy } },
    { text: "PICK", options: { color: P.accent } },
  ], { x: M + 0.9, y: heroY + 0.35, w: 12.5, h: 2.3, fontSize: 150, bold: true });
  T("행사 부스 판매자를 위한 웹 POS 시스템", { x: M + 0.95, y: heroY + 2.9, w: 14, h: 0.9, fontSize: 46, bold: true, color: P.navy });
  T("카드 단말기 · 사업자등록 · 구독료 없이, URL 하나로 시작하는 현장 판매", { x: M + 0.95, y: heroY + 3.9, w: 14, h: 0.7, fontSize: 28, color: P.sub });

  // name card inside hero
  const ncX = M + CW - 6.3, ncY = heroY + 0.7;
  s.addShape(pres.shapes.ROUNDED_RECTANGLE, { x: ncX, y: ncY, w: 5.5, h: 3.6, rectRadius: 0.3, fill: { color: P.white }, line: { color: P.line, width: 1.5 } });
  T("개인 프로젝트", { x: ncX, y: ncY + 0.45, w: 5.5, h: 0.6, fontSize: 26, color: P.muted, align: "center" });
  T("김진서", { x: ncX, y: ncY + 1.15, w: 5.5, h: 1.1, fontSize: 60, bold: true, color: P.navy, align: "center" });
  T("컴퓨터SW 21017019", { x: ncX, y: ncY + 2.45, w: 5.5, h: 0.6, fontSize: 26, color: P.muted, align: "center" });

  // ---------- Section header helper ----------
  const section = (n, title, y) => {
    T(String(n).padStart(2, "0"), { x: M, y: y - 0.02, w: 1.4, h: 1.1, fontSize: 52, bold: true, color: P.accent, valign: "middle" });
    T(title, { x: M + 1.45, y: y - 0.02, w: 12, h: 1.1, fontSize: 52, bold: true, color: P.navy, valign: "middle" });
  };

  // ---------- 1. 개발 목적 ----------
  const s1 = 7.3;
  section(1, "개발 목적", s1);
  T("동인 행사·플리마켓 부스의 거래는 대부분 현금과 계좌이체로 이루어지지만, 기존 모바일 POS는 카드 단말기·사업자등록·월 구독료를 전제로 해 개인 판매자에게 맞지 않습니다.",
    { x: M, y: s1 + 1.3, w: 10.6, h: 1.5, fontSize: 22, color: P.sub, lineSpacingMultiple: 1.15 });

  const probs = [
    [fa.FaCalculator, "암산·거스름돈 실수", "바쁜 현장에서 합계와\n거스름돈 계산 오류"],
    [fa.FaClipboardList, "수기 매출 정리", "행사 종료 후 판매 내역을\n손으로 다시 정리"],
    [fa.FaBoxOpen, "재고 파악 어려움", "남은 수량을 몰라\n품절·과잉 재고 발생"],
    [fa.FaChartPie, "주최측 데이터 부재", "어떤 부스·품목이 잘 팔렸는지\n알 수 없는 주최측"],
  ];
  const pcW = 5.15, pcH = 1.7, pcY0 = s1 + 2.85;
  for (let i = 0; i < probs.length; i++) {
    const [Ic, t, d] = probs[i];
    const x = M + (i % 2) * (pcW + 0.3), y = pcY0 + Math.floor(i / 2) * (pcH + 0.3);
    // text only — no card background or icon
    T(t, { x, y: y + 0.15, w: pcW, h: 0.6, fontSize: 26, bold: true, color: P.navy });
    T(d, { x, y: y + 0.8, w: pcW, h: 0.85, fontSize: 20, color: P.sub, lineSpacingMultiple: 1.1 });
  }

  // comparison table
  const tx = M + 11.1, tw = CW - 11.1;
  const hdr = (t, fill, color) => ({ text: t, options: { bold: true, fill: { color: fill }, color, align: "center" } });
  const rows = [
    [hdr("항목", P.navy, P.white), hdr("기존 POS 앱", P.navy, P.white), hdr("POSPICK", P.navy, P.white)],
    ["사업자등록", "필수", "불필요"],
    ["카드 단말기", "필수", "불필요"],
    ["행사 단위 운영", "없음", "행사별 데이터 분리"],
    ["상품 UI", "텍스트 리스트", "이미지 그리드"],
    ["월 구독료", "월 1~5만 원", "무료"],
    ["설치", "앱스토어 다운로드", "URL 접속만으로 실행"],
    ["지원 환경", "Android 위주", "iOS · Android · PC"],
  ].map((r, i) => i === 0 ? r : [
    { text: r[0], options: { bold: true, color: P.text, fill: { color: P.card } } },
    { text: r[1], options: { color: P.muted, align: "center", fill: { color: P.white } } },
    { text: r[2], options: { bold: true, color: P.navy, align: "center", fill: { color: P.white } } },
  ]);
  s.addTable(rows, {
    x: tx, y: s1 + 1.3, w: tw, colW: [3.0, 3.6, tw - 6.6], rowH: 0.6,
    fontFace: FONT, fontSize: 21, valign: "middle", margin: [0, 0.2, 0, 0.25],
    border: { type: "solid", color: P.line, pt: 1 },
  });

  // ---------- 2. 개발 기술 ----------
  const s2 = 14.35;
  section(2, "개발 기술", s2);
  const stack = [
    ["Frontend", ["React 19", "Vite", "React Router", "Chart.js", "SheetJS"]],
    ["Backend", ["Java", "Spring Boot 4", "Spring Security", "JWT", "JPA"]],
    ["Database", ["MySQL"]],
    ["Cloud · AI", ["AWS S3", "Google Gemini API"]],
  ];
  let sy = s2 + 1.4;
  for (const [label, chips] of stack) {
    // text only — label column + dot-separated list
    T(label, { x: M, y: sy, w: 2.5, h: 0.5, fontSize: 24, bold: true, color: P.navy });
    T(chips.join(" · "), { x: M + 2.5, y: sy + 0.02, w: 8.3, h: 0.95, fontSize: 22, color: P.text, lineSpacingMultiple: 1.15 });
    sy += 1.15;
  }

  // architecture diagram
  const ax = M + 11.1, ay = s2 + 1.4, aw = CW - 11.1, ah = 4.6;
  s.addShape(pres.shapes.ROUNDED_RECTANGLE, { x: ax, y: ay, w: aw, h: ah, rectRadius: 0.25, fill: { color: P.card }, line: { type: "none" } });
  const box = (x, y, w, h, title, sub, fill, color, subColor) => {
    s.addShape(pres.shapes.ROUNDED_RECTANGLE, { x, y, w, h, rectRadius: 0.15, fill: { color: fill }, line: { color: P.line, width: 1 } });
    T(title, { x, y: y + (sub ? 0.18 : 0), w, h: sub ? 0.5 : h, fontSize: 20, bold: true, color, align: "center", valign: sub ? "top" : "middle" });
    if (sub) T(sub, { x: x + 0.1, y: y + 0.68, w: w - 0.2, h: h - 0.8, fontSize: 14, color: subColor || color, align: "center" });
  };
  const arrow = (x1, y1, x2, y2) => s.addShape(pres.shapes.LINE, {
    x: Math.min(x1, x2), y: Math.min(y1, y2), w: Math.abs(x2 - x1), h: Math.abs(y2 - y1),
    flipV: y2 < y1, line: { color: P.muted, width: 2.5, endArrowType: "triangle", beginArrowType: "triangle" },
  });
  const c1 = ax + 0.35, cw1 = 2.1, c2 = c1 + cw1 + 0.5, cw2 = 2.0, c3 = c2 + cw2 + 0.5, cw3 = 2.4, c4 = c3 + cw3 + 0.5, cw4 = aw - (c4 - ax) - 0.35;
  const midY = ay + ah / 2;
  box(c1, ay + 0.45, cw1, 1.55, "SELLER", "판매자\nPOS 화면", P.white, P.navy, P.sub);
  box(c1, ay + 2.4, cw1, 1.55, "ORGANIZER", "주최자\n관리 화면", P.white, P.navy, P.sub);
  box(c2, ay + 0.45, cw2, 3.5, "React", "Vite\nChart.js\nSheetJS", P.white, P.navy, P.sub);
  box(c3, ay + 0.45, cw3, 3.5, "Spring Boot", "REST API\nSpring Security\nJWT 인증\nJPA", P.navy, P.white, "CBD5E1");
  box(c4, ay + 0.45, cw4, 1.0, "MySQL", null, P.white, P.navy);
  box(c4, ay + 1.7, cw4, 1.0, "AWS S3", null, P.white, P.navy);
  box(c4, ay + 2.95, cw4, 1.0, "Gemini API", null, P.white, P.navy);
  arrow(c1 + cw1, ay + 1.22, c2, ay + 1.22);
  arrow(c1 + cw1, ay + 3.17, c2, ay + 3.17);
  arrow(c2 + cw2, midY, c3, midY);
  arrow(c3 + cw3, ay + 0.95, c4, ay + 0.95);
  arrow(c3 + cw3, ay + 2.2, c4, ay + 2.2);
  arrow(c3 + cw3, ay + 3.45, c4, ay + 3.45);

  // ---------- 3. 구현 기능 ----------
  const s3 = 20.85;
  section(3, "구현 기능", s3);

  // user flow strip
  const flow = [
    ["행사 생성", "수집 옵션 설정", "O"],
    ["참가 신청", "데이터 수집 동의", "S"],
    ["승인 / 거절", "신청 목록 관리", "O"],
    ["상품 등록", "S3 업로드 · AI 분류", "S"],
    ["POS 판매", "재고 자동 차감", "S"],
    ["통계 · 정산", "차트 · 엑셀 추출", "B"],
  ];
  const fy = s3 + 1.35, fh = 1.45, gap = 0.42, fw = (CW - gap * (flow.length - 1)) / flow.length;
  flow.forEach(([t, d, who], i) => {
    const x = M + i * (fw + gap);
        const tag = who === "O" ? "주최자" : who === "S" ? "판매자" : "공통";
    const tagC = P.muted;
    // text only — step number, title, role tag, description
    T([
      { text: String(i + 1).padStart(2, "0"), options: { fontSize: 22, bold: true, color: P.accent } },
      { text: "   " + tag, options: { fontSize: 16, color: tagC } },
    ], { x, y: fy + 0.05, w: fw, h: 0.55, valign: "bottom" });
    T(t, { x, y: fy + 0.6, w: fw, h: 0.5, fontSize: 24, bold: true, color: P.navy });
    T(d, { x, y: fy + 1.1, w: fw, h: 0.4, fontSize: 17, color: P.sub });
  });

  // feature cards
  const feats = [
    [fa.FaUserLock, "인증 · 계정", ["회원가입 / 로그인 (JWT)", "ORGANIZER · SELLER 역할 분리", "역할별 API 접근 제어"]],
    [fa.FaCalendarCheck, "행사 관리 (주최자)", ["행사 생성 / 삭제", "매출 · 재고 수집 옵션 설정", "참가 신청 승인 / 거절", "예정 · 진행중 · 종료 자동 표시"]],
    [fa.FaStore, "부스 운영 (판매자)", ["행사 검색 · 참가 신청 (수집 동의)", "상품 CRUD + AWS S3 이미지 업로드", "Gemini AI 카테고리 자동 분류", "재고 부족 시 매진 임박 뱃지"]],
    [fa.FaCashRegister, "POS 결제", ["이미지 그리드로 상품 선택", "현금 / 계좌이체, 거스름돈 자동 계산", "결제 즉시 재고 자동 차감", "주문 취소 시 재고 복구"]],
    [fa.FaChartBar, "통계 대시보드", ["결제 수단별 매출 · 시간대별 피크", "상품별 재고 소진율", "Chart.js 시각화 (Bar · Line · Pie)", "엑셀 내보내기 (SheetJS)"]],
    [fa.FaShieldAlt, "데이터 공유 원칙", ["주최자는 판매자가 동의한 범위만 열람", "동의 시각(agreedAt) DB 기록", "판매자는 자기 부스 데이터 전체 관리"]],
  ];
  const gy = fy + fh + 0.4, leftW = 14.3, cardW = (leftW - 0.3) / 2, cardH = (32.3 - (fy + fh + 0.4) - 0.6) / 3, cgap = 0.3;
  for (let i = 0; i < feats.length; i++) {
    const [Ic, t, items] = feats[i];
    const x = M + (i % 2) * (cardW + 0.3), y = gy + Math.floor(i / 2) * (cardH + cgap);
    // text only — no card background or icon
    T(t, { x, y: y + 0.1, w: cardW, h: 0.65, fontSize: 28, bold: true, color: P.navy, valign: "middle" });
    T(items.map((it, k) => ({ text: it, options: { bullet: { indent: 20 }, breakLine: k < items.length - 1 } })),
      { x, y: y + 0.85, w: cardW - 0.2, h: cardH - 0.9, fontSize: 20, color: P.text, paraSpaceAfter: 2 });
  }

  // screenshot frames
  const shotX = M + leftW + 0.5, shotW = CW - leftW - 0.5;
  // two PC monitor frames stacked vertically (bezel + white screen)
  const labelH = 0.5, vgap = 0.3, bz = 0.12;
  const monH = (3 * cardH + 2 * cgap - vgap - 2 * labelH) / 2;
  [["POS 결제 화면"], ["통계 대시보드"]].forEach(([label], i) => {
    const y = gy + i * (monH + labelH + vgap);
    s.addShape(pres.shapes.ROUNDED_RECTANGLE, { x: shotX, y, w: shotW, h: monH, rectRadius: 0.12, fill: { color: P.navy }, line: { type: "none" }, objectName: `shot_frame_${i + 1}` });
    s.addShape(pres.shapes.RECTANGLE, { x: shotX + bz, y: y + bz, w: shotW - 2 * bz, h: monH - 2 * bz, fill: { color: P.white }, line: { type: "none" }, objectName: `shot_screen_${i + 1}` });
    T("PC 실행 화면 캡처 삽입", { x: shotX + bz, y: y + bz, w: shotW - 2 * bz, h: monH - 2 * bz, fontSize: 20, color: P.muted, align: "center", valign: "middle" });
    T(label, { x: shotX, y: y + monH + 0.08, w: shotW, h: labelH - 0.08, fontSize: 20, bold: true, color: P.navy, align: "center" });
  });

  await pres.writeFile({ fileName: OUT });
  console.log("wrote", OUT);
})();
