const state = { items: [] };
const $ = (selector) => document.querySelector(selector);

async function request(url, options) {
    const response = await fetch(url, { headers: { 'Content-Type': 'application/json' }, ...options });
    if (!response.ok) throw new Error('Request failed');
    return response.status === 204 ? null : response.json();
}

function money(value) { return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(value); }
function statusClass(status) { return status.toLowerCase().replace(' ', '-'); }

function renderItems() {
    const search = $('#search-input').value.toLowerCase();
    const category = $('#category-filter').value;
    const status = $('#status-filter').value;
    const items = state.items.filter(item => (!search || item.name.toLowerCase().includes(search) || item.sku.toLowerCase().includes(search)) && (!category || item.category === category) && (!status || item.status === status));
    $('#inventory-body').innerHTML = items.length ? items.map((item, index) => `<tr><td>${index + 1}</td><td class="product-name"><strong>${item.name}</strong><small class="sku">${item.sku}</small></td><td>${item.category}</td><td>${item.quantity}</td><td>${money(item.unitPrice)}</td><td><button class="row-actions" data-id="${item.id}" aria-label="Adjust stock">Edit</button><button class="delete-action" data-id="${item.id}" aria-label="Delete item">Delete</button></td></tr>`).join('') : '<tr><td colspan="6" class="loading">No products match your filters.</td></tr>';
    $('#result-count').textContent = `Showing ${items.length} of ${state.items.length} items`;
    $('#nav-count').textContent = state.items.length;
    renderStatistics();
}

function renderStatistics() {
    const highestQuantity = Math.max(...state.items.map(item => item.quantity), 1);
    $('#quantity-chart').innerHTML = state.items.map(item => `<div class="bar-row"><span title="${item.name}">${item.name}</span><div class="bar-track"><i style="width:${Math.max((item.quantity / highestQuantity) * 100, 3)}%"></i></div><b>${item.quantity}</b></div>`).join('');
    const categories = state.items.reduce((result, item) => { result[item.category] = (result[item.category] || 0) + 1; return result; }, {});
    $('#category-stats').innerHTML = Object.entries(categories).map(([category, count]) => `<div class="category-row"><span>${category}</span><b>${count} product${count === 1 ? '' : 's'}</b></div>`).join('');
}

async function loadInventory() {
    state.items = await request('/api/inventory');
    const categories = [...new Set(state.items.map(item => item.category))].sort();
    $('#category-filter').innerHTML = '<option value="">All categories</option>' + categories.map(category => `<option>${category}</option>`).join('');
    const summary = await request('/api/inventory/summary');
    $('#total-items').textContent = summary.totalItems;
    $('#units-on-hand').textContent = summary.unitsOnHand.toLocaleString();
    $('#low-stock').textContent = summary.lowStock;
    $('#inventory-value').textContent = money(summary.inventoryValue);
    renderItems();
}

async function loadUser() {
    try {
        const user = await request('/api/auth/me');
        $('#user-name').textContent = user.name;
        const initials = user.name.split(' ').map(part => part[0]).join('').slice(0, 2).toUpperCase();
        $('#user-initials').textContent = initials;
        $('#top-user').textContent = initials;
    } catch (error) {
        window.location.href = '/login';
    }
}

function showToast(message) { const toast = $('#toast'); toast.textContent = message; toast.classList.add('show'); setTimeout(() => toast.classList.remove('show'), 2500); }
function toggleModal(open) { $('#modal-backdrop').hidden = !open; if (open) $('#item-form').elements.name.focus(); }

$('#search-input').addEventListener('input', renderItems);
$('#category-filter').addEventListener('change', renderItems);
$('#status-filter').addEventListener('change', renderItems);
if ($('#open-modal')) $('#open-modal').addEventListener('click', () => toggleModal(true));
if ($('#close-modal')) $('#close-modal').addEventListener('click', () => toggleModal(false));
if ($('#cancel-modal')) $('#cancel-modal').addEventListener('click', () => toggleModal(false));
if ($('#modal-backdrop')) $('#modal-backdrop').addEventListener('click', event => { if (event.target.id === 'modal-backdrop') toggleModal(false); });
$('#export-button').addEventListener('click', () => { const csv = ['SKU,Product,Category,Quantity,Status,Unit price,Supplier', ...state.items.map(item => [item.sku, item.name, item.category, item.quantity, item.status, item.unitPrice, item.supplier].join(','))].join('\n'); const link = document.createElement('a'); link.href = URL.createObjectURL(new Blob([csv], { type: 'text/csv' })); link.download = 'inventory-export.csv'; link.click(); showToast('Inventory exported'); });
const addProductForm = $('#quick-item-form') || $('#item-form');
addProductForm.addEventListener('submit', async event => { event.preventDefault(); const form = new FormData(event.target); const payload = Object.fromEntries(form.entries()); payload.quantity = Number(payload.quantity); payload.reorderLevel = Number(payload.reorderLevel); payload.unitPrice = Number(payload.unitPrice); try { await request('/api/inventory', { method: 'POST', body: JSON.stringify(payload) }); toggleModal(false); event.target.reset(); await loadInventory(); showToast('Product added'); } catch (error) { showToast('Unable to add product'); } });
$('#inventory-body').addEventListener('click', async event => { const button = event.target.closest('.row-actions'); const deleteButton = event.target.closest('.delete-action'); const itemId = button?.dataset.id || deleteButton?.dataset.id; if (!itemId) return; const item = state.items.find(entry => String(entry.id) === itemId); if (deleteButton) { if (!window.confirm(`Delete ${item.name}?`)) return; await request(`/api/inventory/${item.id}`, { method: 'DELETE' }); await loadInventory(); showToast('Product deleted'); return; } const action = window.prompt(`Adjust quantity for ${item.name}:`, '1'); if (action === null || action.trim() === '') return; const amount = Number(action); if (!Number.isInteger(amount)) { showToast('Enter a whole number'); return; } try { await request(`/api/inventory/${item.id}/adjust`, { method: 'PATCH', body: JSON.stringify({ amount }) }); await loadInventory(); showToast('Quantity updated'); } catch (error) { showToast('Quantity cannot be negative'); } });
$('#logout-button').addEventListener('click', async () => { await request('/api/auth/logout', { method: 'POST' }); window.location.href = '/login'; });
loadUser().then(() => loadInventory()).catch(() => showToast('Unable to load inventory'));