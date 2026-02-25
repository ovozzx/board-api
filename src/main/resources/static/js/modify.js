document.addEventListener('DOMContentLoaded', function () {
    const requiredFields = document.querySelectorAll('.required');
    const saveBtn = document.getElementById('save-btn');

    function checkFields() {
        const allFilled = Array.from(requiredFields).every(el =>
            el.value.trim() !== ''
        );
        saveBtn.disabled = !allFilled;
    }

    checkFields();

    requiredFields.forEach(el => {
        el.addEventListener('input', checkFields);
        el.addEventListener('change', checkFields);
    });

    const MAX_FILE_COUNT = 3;
    const fileInputArea = document.getElementById('file-input-area');

    function updateFileInputs() {
        const currentCount = document.querySelectorAll('#existing-files li').length;
        const remainCount = MAX_FILE_COUNT - currentCount;

        fileInputArea.innerHTML = '';

        for (let i = 0; i < remainCount; i++) {
            const input = document.createElement('input');
            input.type = 'file';
            input.name = 'newFiles';
            input.className = 'attachment';
            fileInputArea.appendChild(input);
        }
    }

    updateFileInputs();

    document.addEventListener('click', e => {
        if (e.target.classList.contains('delete-btn')) {
            const li = e.target.closest('li');
            const attachmentId = li.dataset.attachmentId;

            if (attachmentId) {
                const hidden = document.createElement('input');
                hidden.type = 'hidden';
                hidden.name = 'deleteIds';
                hidden.value = attachmentId;
                document.getElementById('deleted-files-area').appendChild(hidden);
            }

            li.remove();
            updateFileInputs();
        }
    });

});