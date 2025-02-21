$(document).ready(() => {
  setEvent();

  const $pwLength = $('#pw-length');
  setPassword($pwLength.val());
});

// 이벤트 설정
const setEvent = () => {
  toggleBtn('upper', 'secondary')
  toggleBtn('lower', 'warning')
  toggleBtn('number', 'success')
  toggleBtn('special', 'info')
  toggleBtn('confuse', 'danger')

  const $pwLength = $('#pw-length');
  $pwLength.on('input', () => {
    const length = $pwLength.val();
    if (length < 1) {
      $pwLength.val('');
      return;
    }
    setPassword(length);
  });
};

// 비밀번호 설정
const setPassword = (length) => {
  $('#password').val(generatePw(length));
}

const copy = () => {
  const $returnText = $('#password');
  const val = $returnText.val();
  if (val.trim().length === 0) {
    return;
  }
  $returnText.select();
  document.execCommand('copy');
  swal('복사되었습니다.', '', 'success');
};

// 버튼 토글 이벤트
const toggleBtn = (id, type) => {
  const $btn = $('#' + id);
  $btn.on('click touchstart', (event) => {
    // 터치 이벤트가 발생했을 때의 기본 동작을 방지
    if (event.type === 'touchstart') {
      event.preventDefault();
    }

    const anyButtonIsY = $('.btn').filter((_, button) => $(button).val() === 'Y').length <= 1;
    if ($btn.val() !== 'N' && anyButtonIsY) {
      return;
    }

    if ($btn.val() === 'Y') {
      $btn.removeClass(`btn-${type}`)
          .addClass(`btn-outline-${type}`)
          .val('N');
    } else {
      $btn.removeClass(`btn-outline-${type}`)
          .addClass(`btn-${type}`)
          .val('Y');
    }
    setPassword($('#pw-length').val());
  });
};

// 비밀번호 생성
const generatePw = (length) => {
  const upperChar = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const lowerChar = 'abcdefghijklmnopqrstuvwxyz';
  const numberChar = '0123456789';
  const specialChar = "!@#$%^&*()-_=+[]{}|;:',.<>?/`~";

  let chars = '';

  if ($('#upper').val() === 'Y') {
    chars += upperChar;
  }
  if ($('#lower').val() === 'Y') {
    chars += lowerChar;
  }
  if ($('#number').val() === 'Y') {
    chars += numberChar;
  }
  if ($('#special').val() === 'Y') {
    chars += specialChar;
  }
  if ($('#confuse').val() === 'Y') {
    const removeChar = "lLIij1!|oO0sS5;:,.`'";
    chars = chars.replace(new RegExp(`[${removeChar}]`, 'g'), '');
  }

  let randomStr = '';
  for (let i = 0; i < length; i++) {
    let randomIdx = Math.floor(Math.random() * chars.length);
    randomStr += chars[randomIdx];
  }

  return randomStr;
}