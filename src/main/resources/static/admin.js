let usersCache = [];
let rolesCache = [];

// Загрузка списка пользователей
async function loadUsers() {
    try {
        const response = await fetch('/api/admin');
        if (!response.ok) throw new Error('Failed to load users');
        const users = await response.json();
        usersCache = users;

        const tbody = document.querySelector("#userList tbody");
        tbody.innerHTML = '';

        users.forEach(user => {
            const roles = user.roles.map(r => r.role.replace('ROLE_', '')).join(', ');
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.age}</td>
                <td>${user.email}</td>
                <td>${roles}</td>
                <td><button class="btn btn-sm btn-info" onclick="openEditModalById(${user.id})">Edit</button></td>
                <td><button class="btn btn-sm btn-danger" onclick="openDeleteModalById(${user.id})">Delete</button></td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        alert(error.message);
    }
}

// Загрузка списка ролей
async function loadRoles(selectElement) {
    try {
        const response = await fetch('/api/admin/roles');
        if (!response.ok) throw new Error('Failed to load roles');
        const roles = await response.json();
        rolesCache = roles;

        selectElement.innerHTML = '';
        roles.forEach(role => {
            const option = document.createElement('option');
            option.value = role.id;
            option.textContent = role.role.replace('ROLE_', '');
            selectElement.appendChild(option);
        });
    } catch (error) {
        alert(error.message);
    }
}

/// Загрузка информации о текущем пользователе (для шапки)
async function loadCurrentUser() {
    try {
        const response = await fetch('/user/current');
        if (!response.ok) throw new Error('Failed to load current user');
        const user = await response.json();

        // Получаем имя пользователя и его роли
        const name = user.name;
        const roles = user.roles.map(role => role.role).join(', '); // Теперь просто выводим ROLE_ADMIN без замены

        // Формируем строку с именем и ролями
        const userInfoText = `${name} (${roles})`;

        // Вставляем данные в шапку
        document.getElementById('principalInfo').textContent = userInfoText;
    } catch (error) {
        console.error('Error loading current user:', error);
        document.getElementById('principalInfo').textContent = 'User info not available';
    }
}


// Открытие модального окна для удаления пользователя
function openDeleteModalById(id) {
    const user = usersCache.find(u => u.id === id);
    if (!user) return;

    document.getElementById("deleteId").value = user.id;
    document.getElementById("deleteName").textContent = user.name;
    document.getElementById("deleteAge").textContent = user.age;
    document.getElementById("deleteEmail").textContent = user.email;
    document.getElementById("deleteRoles").textContent = user.roles.map(r => r.role.replace("ROLE_", "")).join(", ");

    const deleteModal = new bootstrap.Modal(document.getElementById("deleteModal"));
    deleteModal.show();
}

// Открытие модального окна для редактирования пользователя
function openEditModalById(id) {
    const user = usersCache.find(u => u.id === id);
    if (!user) return;

    document.getElementById("editId").value = user.id;
    document.getElementById("editName").value = user.name;
    document.getElementById("editAge").value = user.age;
    document.getElementById("editEmail").value = user.email;
    document.getElementById("editPassword").value = "";

    const select = document.getElementById("editRoles");
    loadRoles(select).then(() => {
        Array.from(select.options).forEach(option => {
            option.selected = user.roles.some(r => r.id === +option.value);
        });
    });

    const editModal = new bootstrap.Modal(document.getElementById("editModal"));
    editModal.show();
}

// Инициализация при загрузке страницы
document.addEventListener("DOMContentLoaded", () => {
    loadUsers();
    loadRoles(document.getElementById("newRoles"));
    loadCurrentUser();

    // Обработчик формы удаления пользователя
    document.getElementById("deleteForm").addEventListener("submit", async e => {
        e.preventDefault();
        const id = document.getElementById("deleteId").value;
        try {
            const response = await fetch(`/api/admin/users/${id}`, {
                method: "DELETE"
            });
            if (!response.ok) throw new Error("Failed to delete user");
            loadUsers();
            bootstrap.Modal.getInstance(document.getElementById("deleteModal")).hide();
        } catch (error) {
            alert(error.message);
        }
    });

    // Обработчик формы добавления пользователя
    document.getElementById("addForm").addEventListener("submit", async e => {
        e.preventDefault();

        const name = document.getElementById("newName").value.trim();
        const age = +document.getElementById("newAge").value;
        const email = document.getElementById("newEmail").value.trim();
        const password = document.getElementById("newPassword").value;
        const selectedOptions = Array.from(document.getElementById("newRoles").selectedOptions);

        if (selectedOptions.length === 0) {
            alert("Please select at least one role");
            return;
        }

        const roleIds = selectedOptions.map(opt => +opt.value);
        const params = new URLSearchParams();
        roleIds.forEach(id => params.append("roleIds", id));

        try {
            const response = await fetch("/api/admin/users?" + params.toString(), {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({name, age, email, password})
            });

            if (!response.ok) throw new Error("Failed to add user");

            document.getElementById("addForm").reset();
            loadUsers();
            document.getElementById("user-list-tab").click();
        } catch (error) {
            alert(error.message);
        }
    });

    // Обработчик формы редактирования пользователя
    document.getElementById("editForm").addEventListener("submit", async e => {
        e.preventDefault();

        const id = +document.getElementById("editId").value;
        const name = document.getElementById("editName").value.trim();
        const age = +document.getElementById("editAge").value;
        const email = document.getElementById("editEmail").value.trim();
        const password = document.getElementById("editPassword").value;
        const selectedOptions = Array.from(document.getElementById("editRoles").selectedOptions);

        if (selectedOptions.length === 0) {
            alert("Please select at least one role");
            return;
        }

        const roleIds = selectedOptions.map(opt => +opt.value);
        const params = new URLSearchParams();
        roleIds.forEach(rid => params.append("roleIds", rid));

        const body = {name, age, email};
        if (password.trim() !== "") {
            body.password = password;
        }

        try {
            const response = await fetch(`/api/admin/users/${id}?${params.toString()}`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to update user: ${response.status} ${errorText}`);
            }

            loadUsers();
            bootstrap.Modal.getInstance(document.getElementById("editModal")).hide();
        } catch (error) {
            alert(error.message);
        }
    });
});
