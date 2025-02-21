$(document).ready(() => {
  toggleLabelOnFocus('textarea');
});

const $qrCode = document.getElementById('qrCode');
const qrCodeSize = $('#qrCodeSize').val();

$qrCode.setAttribute('class', 'hidden');

const qrCode = new QRCode($qrCode, {
  width: qrCodeSize,
  height: qrCodeSize
});

const makeCode = () => {
  const val = $('#textarea').val().trim();

  $('#result-badge').removeClass('hidden');
  $qrCode.setAttribute('class', 'hidden');

  if (val.length > 0) {
    qrCode.makeCode(val);
    $qrCode.setAttribute('class', '');
  }
};
