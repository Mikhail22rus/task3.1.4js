document.addEventListener('DOMContentLoaded', () => {
    const userInfoTbody = document.querySelector('#userInfo tbody');

    async function loadUserInfo() {
        try {
            const response = await fetch('/user/current');
            if (!response.ok) throw new Error('Ошибка загрузки пользователя');

            const user = await response.json();

            userInfoTbody.innerHTML = '';

            const tr = document.createElement('tr');
            tr.innerHTML = `
        <td>${user.id}</td>
        <td>${user.name}</td>
        <td>${user.age}</td>
        <td>${user.email}</td>
        <td>${user.roles.map(role => role.role).join(', ')}</td>
      `;
            userInfoTbody.appendChild(tr);

            const principalInfo = document.getElementById('principalInfo');
            if (principalInfo) {
                principalInfo.textContent = ` ${user.name} (${user.roles.map(r => r.role).join(', ')})`;
            }
        } catch (error) {
            alert(error.message);
        }
    }

    loadUserInfo();
});
