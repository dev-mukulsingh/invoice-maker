/**
 * INVOICE KOUBOU (請求工房)
 * Multi-Template Invoice Generator & Billing Management System
 * Pure Vanilla JavaScript Client Application
 */

document.addEventListener('DOMContentLoaded', () => {
  // Application State
  const state = {
    userProfile: {
      businessName: 'Studio Tonari Creative Workshop',
      email: 'billing@tonari-workshop.com',
      phone: '+91 98765 43210',
      gstNumber: '27AABCT3518Q1ZY',
      address: '404 Windmill Valley, Hinoki District, Pune 411001, MH, India'
    },
    clients: [],
    invoices: [],
    selectedTemplate: 1,
    lineItems: [
      { id: Date.now(), description: 'Handcrafted Cel Animation Artworks', quantity: 3, unitPrice: 4500 }
    ],
    activeModalInvoice: null
  };

  const TEMPLATES = {
    1: { name: 'Classic Ghibli Parchment', class: 'tpl-ghibli', tag: '1. Ghibli' },
    2: { name: 'Vintage Ledger', class: 'tpl-ledger', tag: '2. Ledger' },
    3: { name: 'Cozy Cottage', class: 'tpl-cottage', tag: '3. Cottage' },
    4: { name: 'Tokyo Monospace', class: 'tpl-tokyo', tag: '4. Tokyo' },
    5: { name: 'Sunset Terracotta', class: 'tpl-terracotta', tag: '5. Sunset' },
    6: { name: 'Pastel Kraft', class: 'tpl-kraft', tag: '6. Kraft' },
    7: { name: 'Japanese Hanko Stamp', class: 'tpl-hanko', tag: '7. Hanko 印' },
    8: { name: 'Artisan Blueprint', class: 'tpl-blueprint', tag: '8. Blueprint' },
    9: { name: 'Editorial Notebook', class: 'tpl-notebook', tag: '9. Notebook' },
    10: { name: 'Modern Minimal Chic', class: 'tpl-minimal', tag: '10. Minimal' }
  };

  // DOM Elements
  const tabButtons = document.querySelectorAll('.retro-tab');
  const tabContents = document.querySelectorAll('.tab-content');
  const selectClient = document.getElementById('selectClient');
  const itemsContainer = document.getElementById('itemsContainer');
  const templateGallery = document.getElementById('templateGallery');
  const activeTemplateBadge = document.getElementById('activeTemplateBadge');
  const liveInvoiceSheet = document.getElementById('liveInvoiceSheet');
  const invoiceForm = document.getElementById('invoiceForm');

  // Format currency
  const formatINR = (val) => {
    const num = Number(val) || 0;
    return '₹ ' + num.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  };

  const formatDateStr = (dateStr) => {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    if (isNaN(d)) return dateStr;
    return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
  };

  // =========================================================================
  // INITIALIZATION & TAB ROUTING
  // =========================================================================
  async function init() {
    setupDefaultDates();
    setupEventListeners();
    renderLineItemsForm();
    await fetchUserProfile();
    await fetchClients();
    await fetchDashboardStats();
    await fetchInvoices();
    updateFormCalculations();
    renderLivePreview();
  }

  function setupDefaultDates() {
    const today = new Date();
    const dueDate = new Date();
    dueDate.setDate(today.getDate() + 15);

    const inputInvoiceDate = document.getElementById('inputInvoiceDate');
    const inputDueDate = document.getElementById('inputDueDate');

    if (inputInvoiceDate) inputInvoiceDate.value = today.toISOString().split('T')[0];
    if (inputDueDate) inputDueDate.value = dueDate.toISOString().split('T')[0];
  }

  function switchTab(tabId) {
    tabButtons.forEach(btn => {
      btn.classList.toggle('active', btn.dataset.tab === tabId);
    });
    tabContents.forEach(content => {
      content.classList.toggle('hidden', content.id !== `tab-${tabId}`);
    });

    if (tabId === 'dashboard') fetchDashboardStats();
    if (tabId === 'history') fetchInvoices();
    if (tabId === 'clients') fetchClients();
    if (tabId === 'create') renderLivePreview();
  }

  // =========================================================================
  // API CALLS
  // =========================================================================
  async function fetchUserProfile() {
    try {
      const res = await fetch('/api/user/profile');
      if (res.ok) {
        state.userProfile = await res.json();
        const headerName = document.getElementById('headerBusinessName');
        if (headerName) headerName.textContent = state.userProfile.businessName || 'Business Profile';
      }
    } catch (err) {
      console.error('Error fetching user profile:', err);
    }
  }

  async function fetchClients() {
    try {
      const res = await fetch('/api/clients');
      if (res.ok) {
        state.clients = await res.json();
        populateClientDropdown();
        renderClientsGrid();
      }
    } catch (err) {
      console.error('Error fetching clients:', err);
    }
  }

  async function fetchDashboardStats() {
    try {
      const res = await fetch('/api/dashboard/stats');
      if (res.ok) {
        const stats = await res.json();
        const statRev = document.getElementById('statRevenue');
        const statTot = document.getElementById('statTotalInvoices');
        const statUnpaid = document.getElementById('statUnpaidInvoices');

        if (statRev) statRev.textContent = formatINR(stats.totalRevenue);
        if (statTot) statTot.textContent = stats.totalInvoices || 0;
        if (statUnpaid) statUnpaid.textContent = stats.unpaidInvoices || 0;

        renderDashboardRecentTable(stats.recentInvoices || []);
      }
    } catch (err) {
      console.error('Error fetching dashboard stats:', err);
    }
  }

  async function fetchInvoices() {
    try {
      const res = await fetch('/api/invoices');
      if (res.ok) {
        state.invoices = await res.json();
        renderHistoryTable();
      }
    } catch (err) {
      console.error('Error fetching invoices:', err);
    }
  }

  // =========================================================================
  // CLIENT DROPDOWN & CLIENTS TAB
  // =========================================================================
  function populateClientDropdown() {
    if (!selectClient) return;
    const currentVal = selectClient.value;
    selectClient.innerHTML = '<option value="">-- Choose a registered client --</option>';

    state.clients.forEach(c => {
      const opt = document.createElement('option');
      opt.value = c.id;
      opt.textContent = `${c.clientName} (${c.clientEmail})`;
      selectClient.appendChild(opt);
    });

    if (currentVal && state.clients.some(c => String(c.id) === String(currentVal))) {
      selectClient.value = currentVal;
    } else if (state.clients.length > 0) {
      selectClient.value = state.clients[0].id;
    }
  }

  function renderClientsGrid() {
    const grid = document.getElementById('clientsGrid');
    const badge = document.getElementById('clientCountBadge');
    if (!grid) return;

    if (badge) badge.textContent = `${state.clients.length} Clients`;

    if (state.clients.length === 0) {
      grid.innerHTML = '<div class="col-span-2 text-center py-8 text-gray-500 font-mono text-sm">No clients registered yet. Use the form on the left to add one!</div>';
      return;
    }

    grid.innerHTML = state.clients.map(c => `
      <div class="retro-card-sm p-4 bg-white space-y-2 relative group">
        <div class="flex items-start justify-between">
          <h4 class="font-bold text-[#2B2D42] text-base">${escapeHtml(c.clientName)}</h4>
          <button onclick="deleteClient(${c.id})" class="text-xs text-red-500 hover:text-red-700 font-bold px-2 py-0.5 rounded border border-red-300 hover:bg-red-50" title="Delete client">
            Delete
          </button>
        </div>
        <div class="text-xs text-gray-600 space-y-0.5">
          <div><span class="font-bold">Email:</span> ${escapeHtml(c.clientEmail)}</div>
          ${c.clientPhone ? `<div><span class="font-bold">Phone:</span> ${escapeHtml(c.clientPhone)}</div>` : ''}
          ${c.gstNumber ? `<div><span class="font-bold">GSTIN:</span> <span class="font-mono">${escapeHtml(c.gstNumber)}</span></div>` : ''}
          ${c.billingAddress ? `<div><span class="font-bold">Address:</span> ${escapeHtml(c.billingAddress)}</div>` : ''}
        </div>
      </div>
    `).join('');
  }

  window.deleteClient = async function(id) {
    if (!confirm('Are you sure you want to delete this client? Invoices linked to this client may be affected.')) return;
    try {
      const res = await fetch(`/api/clients/${id}`, { method: 'DELETE' });
      if (res.ok) {
        await fetchClients();
        renderLivePreview();
      } else {
        alert('Failed to delete client. It might be referenced by existing invoices.');
      }
    } catch (err) {
      console.error(err);
    }
  };

  // =========================================================================
  // DYNAMIC LINE ITEMS FORM
  // =========================================================================
  function renderLineItemsForm() {
    if (!itemsContainer) return;
    itemsContainer.innerHTML = '';

    state.lineItems.forEach((item, index) => {
      const row = document.createElement('div');
      row.className = 'grid grid-cols-12 gap-2 items-center bg-[#FAF7F2] p-2 rounded border border-[#2B2D42]';
      row.innerHTML = `
        <div class="col-span-6 sm:col-span-5">
          <input type="text" class="retro-input w-full text-xs item-desc" placeholder="Item description / service" value="${escapeHtml(item.description)}" required />
        </div>
        <div class="col-span-2 sm:col-span-2">
          <input type="number" min="1" class="retro-input w-full text-xs font-mono text-center item-qty" placeholder="Qty" value="${item.quantity}" required />
        </div>
        <div class="col-span-3 sm:col-span-3">
          <input type="number" step="0.01" min="0" class="retro-input w-full text-xs font-mono text-right item-price" placeholder="Price" value="${item.unitPrice}" required />
        </div>
        <div class="col-span-1 sm:col-span-2 flex items-center justify-between sm:justify-end gap-1">
          <span class="hidden sm:inline font-mono text-xs font-bold text-gray-700 item-total-label">${formatINR(item.quantity * item.unitPrice)}</span>
          <button type="button" class="text-red-500 hover:text-red-700 font-bold px-1.5 py-0.5 rounded text-xs btn-remove-row" title="Remove line item">
            &times;
          </button>
        </div>
      `;

      // Event listeners for inputs
      const descInput = row.querySelector('.item-desc');
      const qtyInput = row.querySelector('.item-qty');
      const priceInput = row.querySelector('.item-price');
      const removeBtn = row.querySelector('.btn-remove-row');
      const totalLabel = row.querySelector('.item-total-label');

      descInput.addEventListener('input', (e) => {
        item.description = e.target.value;
        renderLivePreview();
      });

      qtyInput.addEventListener('input', (e) => {
        item.quantity = parseInt(e.target.value, 10) || 0;
        if (totalLabel) totalLabel.textContent = formatINR(item.quantity * item.unitPrice);
        updateFormCalculations();
        renderLivePreview();
      });

      priceInput.addEventListener('input', (e) => {
        item.unitPrice = parseFloat(e.target.value) || 0;
        if (totalLabel) totalLabel.textContent = formatINR(item.quantity * item.unitPrice);
        updateFormCalculations();
        renderLivePreview();
      });

      removeBtn.addEventListener('click', () => {
        if (state.lineItems.length <= 1) {
          alert('An invoice must have at least one line item.');
          return;
        }
        state.lineItems = state.lineItems.filter(it => it.id !== item.id);
        renderLineItemsForm();
        updateFormCalculations();
        renderLivePreview();
      });

      itemsContainer.appendChild(row);
    });
  }

  function addLineItem() {
    state.lineItems.push({
      id: Date.now(),
      description: 'Artisan Goods / Creative Service',
      quantity: 1,
      unitPrice: 1000
    });
    renderLineItemsForm();
    updateFormCalculations();
    renderLivePreview();
  }

  function calculateTotals() {
    let subtotal = 0;
    state.lineItems.forEach(it => {
      subtotal += (Number(it.quantity) || 0) * (Number(it.unitPrice) || 0);
    });
    const cgst = Math.round(subtotal * 0.09 * 100) / 100;
    const sgst = Math.round(subtotal * 0.09 * 100) / 100;
    const grandTotal = Math.round((subtotal + cgst + sgst) * 100) / 100;
    return { subtotal, cgst, sgst, grandTotal };
  }

  function updateFormCalculations() {
    const { subtotal, cgst, sgst, grandTotal } = calculateTotals();
    const formSubtotal = document.getElementById('formSubtotal');
    const formCGST = document.getElementById('formCGST');
    const formSGST = document.getElementById('formSGST');
    const formGrandTotal = document.getElementById('formGrandTotal');

    if (formSubtotal) formSubtotal.textContent = formatINR(subtotal);
    if (formCGST) formCGST.textContent = formatINR(cgst);
    if (formSGST) formSGST.textContent = formatINR(sgst);
    if (formGrandTotal) formGrandTotal.textContent = formatINR(grandTotal);
  }

  // =========================================================================
  // LIVE PREVIEW HTML RENDERING (10 TEMPLATES)
  // =========================================================================
  function renderLivePreview() {
    if (!liveInvoiceSheet) return;

    const tpl = TEMPLATES[state.selectedTemplate] || TEMPLATES[1];
    liveInvoiceSheet.className = `invoice-sheet ${tpl.class}`;

    const clientId = selectClient ? selectClient.value : null;
    const client = state.clients.find(c => String(c.id) === String(clientId)) || {
      clientName: 'Sample Client Enterprise',
      clientEmail: 'client@example.com',
      clientPhone: '+91 98000 00000',
      billingAddress: '123 Market Square, District 4',
      gstNumber: '27AAAAA0000A1Z5'
    };

    const invoiceNum = (document.getElementById('inputInvoiceNumber')?.value.trim()) || 'INV-2026-DRAFT';
    const invoiceDate = (document.getElementById('inputInvoiceDate')?.value) || new Date().toISOString().split('T')[0];
    const dueDate = (document.getElementById('inputDueDate')?.value) || new Date().toISOString().split('T')[0];
    const status = (document.getElementById('selectStatus')?.value) || 'UNPAID';
    const notes = (document.getElementById('inputNotes')?.value) || 'Thank you for choosing Studio Tonari! Deliveries packed with utmost care.';
    const { subtotal, cgst, sgst, grandTotal } = calculateTotals();

    // Status Badge Markup
    let statusMarkup = '';
    if (state.selectedTemplate === 7) {
      // Traditional Japanese Hanko Seal
      const hankoText = status === 'PAID' ? '領収済' : (status === 'UNPAID' ? '未払請求' : '見積控');
      statusMarkup = `<div class="tpl-hanko-seal font-display">${hankoText}</div>`;
    } else {
      if (status === 'PAID') {
        statusMarkup = `<span class="retro-badge retro-badge-paid font-mono">✔ PAID / 領収済</span>`;
      } else if (status === 'UNPAID') {
        statusMarkup = `<span class="retro-badge retro-badge-unpaid font-mono">✘ UNPAID / 未払</span>`;
      } else {
        statusMarkup = `<span class="retro-badge retro-badge-draft font-mono">✎ DRAFT</span>`;
      }
    }

    // Line items HTML
    const itemsHtml = state.lineItems.map((item, idx) => `
      <tr>
        <td class="p-2 text-center text-xs font-mono">${idx + 1}</td>
        <td class="p-2 text-xs font-medium">${escapeHtml(item.description || 'Line Item')}</td>
        <td class="p-2 text-center text-xs font-mono">${item.quantity || 1}</td>
        <td class="p-2 text-right text-xs font-mono">${formatINR(item.unitPrice || 0)}</td>
        <td class="p-2 text-right text-xs font-mono font-bold">${formatINR((item.quantity || 1) * (item.unitPrice || 0))}</td>
      </tr>
    `).join('');

    liveInvoiceSheet.innerHTML = `
      <!-- Header -->
      <div class="flex flex-col sm:flex-row justify-between items-start gap-4 pb-4 border-b-2 border-[#2B2D42]">
        <div>
          <h3 class="tpl-header-title leading-tight">${escapeHtml(state.userProfile.businessName)}</h3>
          <p class="text-xs text-gray-600 mt-1">${escapeHtml(state.userProfile.address || '')}</p>
          <p class="text-xs text-gray-600">
            ${state.userProfile.phone ? `Phone: ${escapeHtml(state.userProfile.phone)} | ` : ''}
            Email: ${escapeHtml(state.userProfile.email || '')}
          </p>
          ${state.userProfile.gstNumber ? `<p class="text-xs font-bold font-mono text-[#2B2D42]">GSTIN: ${escapeHtml(state.userProfile.gstNumber)}</p>` : ''}
        </div>
        <div class="text-right self-end sm:self-auto flex flex-col items-end">
          <div class="text-xl font-black font-display tracking-wider text-[#2B2D42]">TAX INVOICE</div>
          <div class="text-sm font-bold font-mono text-[#D46A43] mt-0.5">#${escapeHtml(invoiceNum)}</div>
          <div class="mt-2">${statusMarkup}</div>
        </div>
      </div>

      <!-- Bill To & Meta -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 py-4 border-b border-gray-300">
        <div>
          <div class="text-[10px] font-bold uppercase tracking-wider text-gray-500">BILLED TO:</div>
          <div class="text-sm font-bold text-[#2B2D42] mt-0.5">${escapeHtml(client.clientName)}</div>
          <div class="text-xs text-gray-600 mt-0.5">${escapeHtml(client.billingAddress || '')}</div>
          <div class="text-xs text-gray-600">
            ${client.clientPhone ? `Tel: ${escapeHtml(client.clientPhone)} | ` : ''}
            ${escapeHtml(client.clientEmail || '')}
          </div>
          ${client.gstNumber ? `<div class="text-xs font-mono font-bold text-gray-700 mt-0.5">GSTIN: ${escapeHtml(client.gstNumber)}</div>` : ''}
        </div>
        <div class="space-y-1 font-mono text-xs sm:text-right">
          <div><span class="text-gray-500">Invoice Date:</span> <span class="font-bold">${formatDateStr(invoiceDate)}</span></div>
          <div><span class="text-gray-500">Due Date:</span> <span class="font-bold">${formatDateStr(dueDate)}</span></div>
          <div><span class="text-gray-500">Template Style:</span> <span class="font-bold text-[#3A506B]">${tpl.name}</span></div>
        </div>
      </div>

      <!-- Items Table -->
      <div class="my-4 overflow-x-auto">
        <table class="w-full tpl-table text-left border-collapse">
          <thead>
            <tr class="text-xs">
              <th class="p-2 text-center w-10">#</th>
              <th class="p-2">Description</th>
              <th class="p-2 text-center w-16">Qty</th>
              <th class="p-2 text-right w-28">Unit Price</th>
              <th class="p-2 text-right w-32">Total</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            ${itemsHtml}
          </tbody>
        </table>
      </div>

      <!-- Totals & Notes -->
      <div class="grid grid-cols-1 sm:grid-cols-12 gap-4 items-start pt-2">
        <div class="sm:col-span-7 space-y-1">
          <div class="text-[10px] font-bold uppercase tracking-wider text-gray-500">Notes &amp; Instructions:</div>
          <div class="text-xs text-gray-600 leading-relaxed bg-[#FAF7F2] p-2.5 rounded border border-dashed border-[#2B2D42] font-mono">
            ${escapeHtml(notes)}
          </div>
        </div>
        <div class="sm:col-span-5">
          <div class="tpl-total-box p-3 rounded space-y-1.5 font-mono text-xs">
            <div class="flex justify-between">
              <span class="text-gray-600">Subtotal:</span>
              <span class="font-bold">${formatINR(subtotal)}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">CGST (9%):</span>
              <span>${formatINR(cgst)}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">SGST (9%):</span>
              <span>${formatINR(sgst)}</span>
            </div>
            <div class="border-t border-[#2B2D42] pt-1.5 flex justify-between font-bold text-sm text-[#2B2D42]">
              <span>Grand Total:</span>
              <span class="text-base font-black">${formatINR(grandTotal)}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer Stamp / Signatory -->
      <div class="mt-8 pt-4 border-t border-gray-300 flex justify-between items-end text-xs text-gray-500">
        <div>
          <div>Generated by Multi-Template Invoice Koubou.</div>
          <div>Thank you for your partnership! 🌿</div>
        </div>
        <div class="text-center font-mono">
          <div class="border-b border-dashed border-[#2B2D42] w-36 mb-1"></div>
          <div class="text-[10px] font-bold text-[#2B2D42]">Authorized Signature</div>
        </div>
      </div>
    `;
  }

  // =========================================================================
  // SAVING & DOWNLOADING INVOICES
  // =========================================================================
  async function handleSaveInvoice(downloadPdfAfter = false) {
    const clientId = selectClient ? selectClient.value : null;
    if (!clientId) {
      alert('Please select a valid client before saving.');
      return;
    }

    if (state.lineItems.length === 0) {
      alert('Please add at least one line item.');
      return;
    }

    for (let it of state.lineItems) {
      if (!it.description || !it.description.trim()) {
        alert('Please ensure all line items have a valid description.');
        return;
      }
      if (!it.quantity || it.quantity < 1) {
        alert('Item quantity must be at least 1.');
        return;
      }
      if (it.unitPrice === undefined || it.unitPrice < 0) {
        alert('Item price must be non-negative.');
        return;
      }
    }

    const payload = {
      invoiceNumber: document.getElementById('inputInvoiceNumber')?.value.trim() || null,
      clientId: Number(clientId),
      invoiceDate: document.getElementById('inputInvoiceDate')?.value,
      dueDate: document.getElementById('inputDueDate')?.value,
      selectedTemplate: Number(state.selectedTemplate),
      status: document.getElementById('selectStatus')?.value || 'UNPAID',
      notes: document.getElementById('inputNotes')?.value || null,
      items: state.lineItems.map(it => ({
        description: it.description.trim(),
        quantity: Number(it.quantity),
        unitPrice: Number(it.unitPrice)
      }))
    };

    try {
      const res = await fetch('/api/invoices', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!res.ok) {
        const errorData = await res.json();
        alert('Error saving invoice: ' + (errorData.message || 'Validation failed'));
        return;
      }

      const savedInvoice = await res.json();

      if (downloadPdfAfter) {
        window.open(`/api/invoices/${savedInvoice.id}/pdf`, '_blank');
      }

      alert(`Invoice #${savedInvoice.invoiceNumber} successfully created!`);
      await fetchDashboardStats();
      await fetchInvoices();
      switchTab('history');
    } catch (err) {
      console.error(err);
      alert('An error occurred while communicating with the server.');
    }
  }

  // =========================================================================
  // TABLES: DASHBOARD & HISTORY
  // =========================================================================
  function renderDashboardRecentTable(invoices) {
    const tbody = document.getElementById('dashboardRecentTableBody');
    if (!tbody) return;

    if (!invoices || invoices.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" class="p-4 text-center text-gray-500 font-mono text-xs">No invoices generated yet.</td></tr>';
      return;
    }

    tbody.innerHTML = invoices.map(inv => `
      <tr class="hover:bg-[#FAF7F2] transition">
        <td class="p-3 font-mono font-bold text-[#2B2D42]">#${escapeHtml(inv.invoiceNumber)}</td>
        <td class="p-3 font-medium">${escapeHtml(inv.client ? inv.client.clientName : 'Unknown')}</td>
        <td class="p-3 text-xs font-mono text-gray-600">${formatDateStr(inv.invoiceDate)}</td>
        <td class="p-3 text-xs font-medium text-[#3A506B]">${escapeHtml(inv.templateName || 'Ghibli')}</td>
        <td class="p-3 text-right font-mono font-bold text-[#2B2D42]">${formatINR(inv.grandTotal)}</td>
        <td class="p-3 text-center">
          <button onclick="toggleInvoiceStatus(${inv.id}, '${inv.status}')" class="cursor-pointer" title="Click to toggle status">
            ${inv.status === 'PAID' ? '<span class="retro-badge retro-badge-paid font-mono">PAID</span>' : '<span class="retro-badge retro-badge-unpaid font-mono">UNPAID</span>'}
          </button>
        </td>
        <td class="p-3 text-center">
          <div class="flex items-center justify-center gap-1.5">
            <a href="/api/invoices/${inv.id}/pdf" target="_blank" class="retro-btn retro-btn-sm retro-btn-terracotta" title="Download PDF">PDF</a>
            <button onclick="openModalPreview(${inv.id})" class="retro-btn retro-btn-sm retro-btn-outline" title="Preview">View</button>
          </div>
        </td>
      </tr>
    `).join('');
  }

  function renderHistoryTable() {
    const tbody = document.getElementById('historyTableBody');
    const filterSearch = document.getElementById('filterSearch')?.value.toLowerCase().trim() || '';
    const filterStatus = document.getElementById('filterStatus')?.value || 'ALL';
    if (!tbody) return;

    const filtered = state.invoices.filter(inv => {
      const matchStatus = filterStatus === 'ALL' || inv.status === filterStatus;
      const clientName = inv.client?.clientName?.toLowerCase() || '';
      const invNum = inv.invoiceNumber?.toLowerCase() || '';
      const matchSearch = !filterSearch || clientName.includes(filterSearch) || invNum.includes(filterSearch);
      return matchStatus && matchSearch;
    });

    if (filtered.length === 0) {
      tbody.innerHTML = '<tr><td colspan="8" class="p-6 text-center text-gray-500 font-mono text-xs">No matching invoices found.</td></tr>';
      return;
    }

    tbody.innerHTML = filtered.map(inv => `
      <tr class="hover:bg-[#FAF7F2] transition">
        <td class="p-3 font-mono font-bold text-[#2B2D42]">#${escapeHtml(inv.invoiceNumber)}</td>
        <td class="p-3 font-medium">${escapeHtml(inv.client?.clientName || 'Unknown')}</td>
        <td class="p-3 text-xs font-mono text-gray-600">${formatDateStr(inv.invoiceDate)}</td>
        <td class="p-3 text-xs font-mono text-gray-600">${formatDateStr(inv.dueDate)}</td>
        <td class="p-3 text-xs text-[#3A506B] font-medium">${escapeHtml(inv.templateName || 'Classic')}</td>
        <td class="p-3 text-right font-mono font-bold text-[#2B2D42]">${formatINR(inv.grandTotal)}</td>
        <td class="p-3 text-center">
          <button onclick="toggleInvoiceStatus(${inv.id}, '${inv.status}')" class="cursor-pointer" title="Click to toggle status">
            ${inv.status === 'PAID' ? '<span class="retro-badge retro-badge-paid font-mono">PAID</span>' : (inv.status === 'UNPAID' ? '<span class="retro-badge retro-badge-unpaid font-mono">UNPAID</span>' : '<span class="retro-badge retro-badge-draft font-mono">DRAFT</span>')}
          </button>
        </td>
        <td class="p-3 text-center">
          <div class="flex items-center justify-center gap-1.5">
            <a href="/api/invoices/${inv.id}/pdf" target="_blank" class="retro-btn retro-btn-sm retro-btn-terracotta" title="Download PDF">PDF</a>
            <button onclick="openModalPreview(${inv.id})" class="retro-btn retro-btn-sm retro-btn-outline" title="Preview">View</button>
            <button onclick="deleteInvoice(${inv.id})" class="text-red-500 hover:text-red-700 font-bold px-2 py-1 text-xs" title="Delete">&times;</button>
          </div>
        </td>
      </tr>
    `).join('');
  }

  window.toggleInvoiceStatus = async function(id, currentStatus) {
    const newStatus = currentStatus === 'PAID' ? 'UNPAID' : 'PAID';
    try {
      const res = await fetch(`/api/invoices/${id}/status`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: newStatus })
      });
      if (res.ok) {
        await fetchDashboardStats();
        await fetchInvoices();
      }
    } catch (err) {
      console.error(err);
    }
  };

  window.deleteInvoice = async function(id) {
    if (!confirm('Are you sure you want to delete this invoice?')) return;
    try {
      const res = await fetch(`/api/invoices/${id}`, { method: 'DELETE' });
      if (res.ok) {
        await fetchDashboardStats();
        await fetchInvoices();
      }
    } catch (err) {
      console.error(err);
    }
  };

  window.openModalPreview = function(id) {
    const invoice = state.invoices.find(i => i.id === id);
    if (!invoice) return;

    state.activeModalInvoice = invoice;
    const modal = document.getElementById('previewModal');
    const container = document.getElementById('modalSheetContainer');
    const badge = document.getElementById('previewModalTemplateName');

    if (badge) badge.textContent = invoice.templateName || 'Classic Template';

    const tpl = TEMPLATES[invoice.selectedTemplate] || TEMPLATES[1];

    let statusMarkup = '';
    if (invoice.selectedTemplate === 7) {
      const hankoText = invoice.status === 'PAID' ? '領収済' : '未払請求';
      statusMarkup = `<div class="tpl-hanko-seal font-display">${hankoText}</div>`;
    } else {
      statusMarkup = invoice.status === 'PAID'
        ? `<span class="retro-badge retro-badge-paid font-mono">✔ PAID / 領収済</span>`
        : `<span class="retro-badge retro-badge-unpaid font-mono">✘ UNPAID / 未払</span>`;
    }

    const itemsHtml = (invoice.items || []).map((it, idx) => `
      <tr>
        <td class="p-2 text-center text-xs font-mono">${idx + 1}</td>
        <td class="p-2 text-xs font-medium">${escapeHtml(it.description)}</td>
        <td class="p-2 text-center text-xs font-mono">${it.quantity}</td>
        <td class="p-2 text-right text-xs font-mono">${formatINR(it.unitPrice)}</td>
        <td class="p-2 text-right text-xs font-mono font-bold">${formatINR(it.itemTotal)}</td>
      </tr>
    `).join('');

    container.innerHTML = `
      <div class="invoice-sheet ${tpl.class}">
        <div class="flex flex-col sm:flex-row justify-between items-start gap-4 pb-4 border-b-2 border-[#2B2D42]">
          <div>
            <h3 class="tpl-header-title leading-tight">${escapeHtml(invoice.user?.businessName || state.userProfile.businessName)}</h3>
            <p class="text-xs text-gray-600 mt-1">${escapeHtml(invoice.user?.address || state.userProfile.address || '')}</p>
            <p class="text-xs text-gray-600">Email: ${escapeHtml(invoice.user?.email || state.userProfile.email || '')}</p>
            ${invoice.user?.gstNumber ? `<p class="text-xs font-bold font-mono text-[#2B2D42]">GSTIN: ${escapeHtml(invoice.user.gstNumber)}</p>` : ''}
          </div>
          <div class="text-right self-end sm:self-auto flex flex-col items-end">
            <div class="text-xl font-black font-display tracking-wider text-[#2B2D42]">TAX INVOICE</div>
            <div class="text-sm font-bold font-mono text-[#D46A43] mt-0.5">#${escapeHtml(invoice.invoiceNumber)}</div>
            <div class="mt-2">${statusMarkup}</div>
          </div>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 py-4 border-b border-gray-300">
          <div>
            <div class="text-[10px] font-bold uppercase tracking-wider text-gray-500">BILLED TO:</div>
            <div class="text-sm font-bold text-[#2B2D42] mt-0.5">${escapeHtml(invoice.client?.clientName || 'Client')}</div>
            <div class="text-xs text-gray-600 mt-0.5">${escapeHtml(invoice.client?.billingAddress || '')}</div>
            <div class="text-xs text-gray-600">${escapeHtml(invoice.client?.clientEmail || '')}</div>
            ${invoice.client?.gstNumber ? `<div class="text-xs font-mono font-bold text-gray-700 mt-0.5">GSTIN: ${escapeHtml(invoice.client.gstNumber)}</div>` : ''}
          </div>
          <div class="space-y-1 font-mono text-xs sm:text-right">
            <div><span class="text-gray-500">Invoice Date:</span> <span class="font-bold">${formatDateStr(invoice.invoiceDate)}</span></div>
            <div><span class="text-gray-500">Due Date:</span> <span class="font-bold">${formatDateStr(invoice.dueDate)}</span></div>
            <div><span class="text-gray-500">Template:</span> <span class="font-bold text-[#3A506B]">${tpl.name}</span></div>
          </div>
        </div>

        <div class="my-4 overflow-x-auto">
          <table class="w-full tpl-table text-left border-collapse">
            <thead>
              <tr class="text-xs">
                <th class="p-2 text-center w-10">#</th>
                <th class="p-2">Description</th>
                <th class="p-2 text-center w-16">Qty</th>
                <th class="p-2 text-right w-28">Unit Price</th>
                <th class="p-2 text-right w-32">Total</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              ${itemsHtml}
            </tbody>
          </table>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-12 gap-4 items-start pt-2">
          <div class="sm:col-span-7 space-y-1">
            <div class="text-[10px] font-bold uppercase tracking-wider text-gray-500">Notes &amp; Instructions:</div>
            <div class="text-xs text-gray-600 leading-relaxed bg-[#FAF7F2] p-2.5 rounded border border-dashed border-[#2B2D42] font-mono">
              ${escapeHtml(invoice.notes || 'No extra notes.')}
            </div>
          </div>
          <div class="sm:col-span-5">
            <div class="tpl-total-box p-3 rounded space-y-1.5 font-mono text-xs">
              <div class="flex justify-between">
                <span class="text-gray-600">Subtotal:</span>
                <span class="font-bold">${formatINR(invoice.subtotal)}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">CGST (9%):</span>
                <span>${formatINR(invoice.cgst)}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">SGST (9%):</span>
                <span>${formatINR(invoice.sgst)}</span>
              </div>
              <div class="border-t border-[#2B2D42] pt-1.5 flex justify-between font-bold text-sm text-[#2B2D42]">
                <span>Grand Total:</span>
                <span class="text-base font-black">${formatINR(invoice.grandTotal)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    `;

    modal.showModal();
  };

  // =========================================================================
  // EVENT LISTENERS SETUP
  // =========================================================================
  function setupEventListeners() {
    // Tab switching
    tabButtons.forEach(btn => {
      btn.addEventListener('click', () => switchTab(btn.dataset.tab));
    });

    // Quick New Invoice button from masthead
    document.getElementById('btnQuickCreateInvoice')?.addEventListener('click', () => {
      switchTab('create');
    });

    // Quick Add Client from Dashboard
    document.getElementById('btnDashboardAddClient')?.addEventListener('click', () => {
      switchTab('clients');
    });

    // Refresh Dashboard
    document.getElementById('btnRefreshDashboard')?.addEventListener('click', () => {
      fetchDashboardStats();
    });

    // Template Gallery Selection
    if (templateGallery) {
      templateGallery.querySelectorAll('.template-card').forEach(card => {
        card.addEventListener('click', () => {
          templateGallery.querySelectorAll('.template-card').forEach(c => c.classList.remove('active'));
          card.classList.add('active');
          const tplId = Number(card.dataset.tpl);
          state.selectedTemplate = tplId;
          const tpl = TEMPLATES[tplId] || TEMPLATES[1];
          if (activeTemplateBadge) activeTemplateBadge.textContent = `#${tplId} ${tpl.name}`;
          renderLivePreview();
        });
      });
    }

    // Add Line Item Button
    document.getElementById('btnAddItemRow')?.addEventListener('click', addLineItem);

    // Form inputs change triggers
    selectClient?.addEventListener('change', renderLivePreview);
    document.getElementById('inputInvoiceNumber')?.addEventListener('input', renderLivePreview);
    document.getElementById('inputInvoiceDate')?.addEventListener('input', renderLivePreview);
    document.getElementById('inputDueDate')?.addEventListener('input', renderLivePreview);
    document.getElementById('selectStatus')?.addEventListener('change', renderLivePreview);
    document.getElementById('inputNotes')?.addEventListener('input', renderLivePreview);

    // Form Submissions
    invoiceForm?.addEventListener('submit', (e) => {
      e.preventDefault();
      handleSaveInvoice(false);
    });

    document.getElementById('btnSaveAndDownloadPdf')?.addEventListener('click', () => {
      handleSaveInvoice(true);
    });

    document.getElementById('btnResetForm')?.addEventListener('click', () => {
      invoiceForm.reset();
      setupDefaultDates();
      state.lineItems = [
        { id: Date.now(), description: 'Handcrafted Cel Animation Artworks', quantity: 3, unitPrice: 4500 }
      ];
      renderLineItemsForm();
      updateFormCalculations();
      renderLivePreview();
    });

    // History filter inputs
    document.getElementById('filterSearch')?.addEventListener('input', renderHistoryTable);
    document.getElementById('filterStatus')?.addEventListener('change', renderHistoryTable);

    // Fullscreen Preview button
    document.getElementById('btnFullscreenPreview')?.addEventListener('click', () => {
      const modal = document.getElementById('previewModal');
      const container = document.getElementById('modalSheetContainer');
      const badge = document.getElementById('previewModalTemplateName');
      const tpl = TEMPLATES[state.selectedTemplate] || TEMPLATES[1];

      if (badge) badge.textContent = `${tpl.name} (Live View)`;
      if (container && liveInvoiceSheet) {
        container.innerHTML = liveInvoiceSheet.outerHTML;
      }
      modal?.showModal();
    });

    // Close preview modal
    document.getElementById('btnClosePreviewModal')?.addEventListener('click', () => {
      document.getElementById('previewModal')?.close();
    });

    // Modal print & download PDF
    document.getElementById('btnModalPrint')?.addEventListener('click', () => {
      window.print();
    });

    document.getElementById('btnModalDownloadPdf')?.addEventListener('click', () => {
      if (state.activeModalInvoice) {
        window.open(`/api/invoices/${state.activeModalInvoice.id}/pdf`, '_blank');
      } else {
        alert('Please save the invoice to download the official vector PDF.');
      }
    });

    // Client Creation Form
    document.getElementById('clientForm')?.addEventListener('submit', async (e) => {
      e.preventDefault();
      const payload = {
        clientName: document.getElementById('newClientName').value.trim(),
        clientEmail: document.getElementById('newClientEmail').value.trim(),
        clientPhone: document.getElementById('newClientPhone').value.trim() || null,
        gstNumber: document.getElementById('newClientGST').value.trim() || null,
        billingAddress: document.getElementById('newClientAddress').value.trim() || null
      };

      try {
        const res = await fetch('/api/clients', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        if (res.ok) {
          alert('Client created successfully!');
          document.getElementById('clientForm').reset();
          await fetchClients();
          switchTab('create');
        } else {
          const err = await res.json();
          alert('Error: ' + (err.message || 'Could not save client'));
        }
      } catch (err) {
        console.error(err);
      }
    });

    document.getElementById('btnOpenNewClientFromForm')?.addEventListener('click', () => {
      switchTab('clients');
    });

    // Business Profile Modal
    const profileModal = document.getElementById('profileModal');
    document.getElementById('btnOpenProfile')?.addEventListener('click', () => {
      document.getElementById('profBusinessName').value = state.userProfile.businessName || '';
      document.getElementById('profEmail').value = state.userProfile.email || '';
      document.getElementById('profPhone').value = state.userProfile.phone || '';
      document.getElementById('profGst').value = state.userProfile.gstNumber || '';
      document.getElementById('profAddress').value = state.userProfile.address || '';
      profileModal?.showModal();
    });

    document.getElementById('btnCloseProfileModal')?.addEventListener('click', () => profileModal?.close());
    document.getElementById('btnCancelProfile')?.addEventListener('click', () => profileModal?.close());

    document.getElementById('profileForm')?.addEventListener('submit', async (e) => {
      e.preventDefault();
      const payload = {
        businessName: document.getElementById('profBusinessName').value.trim(),
        email: document.getElementById('profEmail').value.trim(),
        phone: document.getElementById('profPhone').value.trim() || null,
        gstNumber: document.getElementById('profGst').value.trim() || null,
        address: document.getElementById('profAddress').value.trim() || null
      };

      try {
        const res = await fetch('/api/user/profile', {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        if (res.ok) {
          state.userProfile = await res.json();
          const headerName = document.getElementById('headerBusinessName');
          if (headerName) headerName.textContent = state.userProfile.businessName;
          profileModal?.close();
          renderLivePreview();
        } else {
          alert('Could not update profile.');
        }
      } catch (err) {
        console.error(err);
      }
    });
  }

  // Escape HTML helper for security
  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  // Start Application
  init();
});
