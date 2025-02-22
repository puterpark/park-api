$(document).ready(() => {
  setEvent();

  const $pwLength = $('#pw-length');
  setPassword($pwLength.val());
});

// 이벤트 설정
const setEvent = () => {
  onChangeCheckbox('upper');
  onChangeCheckbox('lower');
  onChangeCheckbox('number');
  onChangeCheckbox('special');
  onChangeCheckbox('confuse');

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

// 체크박스 변경 이벤트
const onChangeCheckbox = (id) => {
  const $el = $('#' + id);
  $el.on('change', function () {
    const $spans = $(this).siblings('span');
    const $checkboxes = $('.form-check-input'); // 모든 체크박스 선택
    const checkedCount = $checkboxes.filter(':checked').length; // 체크된 개수 확인
    const otherCheckedCount = $checkboxes.not('#confuse').filter(':checked').length; // confuse 제외 체크된 개수

    if ($(this).is(':checked')) {
      $spans.css('color', '');
    } else {
      // 체크 해제 전에 최소 하나의 체크박스가 남아 있는지 확인
      if (checkedCount === 0) {
        $(this).prop('checked', true); // 다시 체크 상태로 복구
        return;
      }

      if ($('#confuse').is(':checked') && otherCheckedCount === 0) {
        $(this).prop('checked', true); // 다시 체크 상태로 복구
        return;
      }

      $spans.css('color', 'var(--primary) !important');
    }

    setPassword($('#pw-length').val());
  });
}

// 비밀번호 생성
const generatePw = (length) => {
  const upperChar = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const lowerChar = 'abcdefghijklmnopqrstuvwxyz';
  const numberChar = '0123456789';
  const specialChar = "!@#$%^&*()-_=+[]{}|;:',.<>?/`~";

  let chars = '';

  if ($('#upper').is(':checked')) {
    chars += upperChar;
  }
  if ($('#lower').is(':checked')) {
    chars += lowerChar;
  }
  if ($('#number').is(':checked')) {
    chars += numberChar;
  }
  if ($('#special').is(':checked')) {
    chars += specialChar;
  }
  if ($('#confuse').is(':checked')) {
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