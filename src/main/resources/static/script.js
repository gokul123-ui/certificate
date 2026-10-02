// Digital Credential Platform Frontend Logic
document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    loadInstitutions();
    loadInvoices();
    loadReports();
});

// Toast notification helper
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast toast-${type}`;
    toast.classList.remove('hidden');
    setTimeout(() => {
        toast.classList.add('hidden');
    }, 4000);
}

// Tab navigation system
function initTabs() {
    const navButtons = document.querySelectorAll('.nav-btn');
    const tabPages = document.querySelectorAll('.tab-page');

    navButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetTab = btn.getAttribute('data-tab');

            navButtons.forEach(b => b.classList.remove('active'));
            tabPages.forEach(p => p.classList.remove('active'));

            btn.classList.add('active');
            document.getElementById(targetTab).classList.add('active');

            // Refresh data on tab activation
            if (targetTab === 'tab-institution') loadInstitutions();
            if (targetTab === 'tab-billing') loadInvoices();
            if (targetTab === 'tab-reports') loadReports();
        });
    });
}

// -------------------------------------------------------------
// INSTITUTION MANAGEMENT
// -------------------------------------------------------------
async function registerInstitution(event) {
    event.preventDefault();
    const name = document.getElementById('instName').value.trim();
    const email = document.getElementById('instEmail').value.trim();
    const address = document.getElementById('instAddress').value.trim();
    const resultBox = document.getElementById('instResult');

    if (!name || !email) {
        showToast('Please provide both Institution Name and Email', 'error');
        return;
    }

    try {
        const response = await fetch('/api/institutions', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, address })
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Institution registered successfully!');
            resultBox.className = 'result-box alert-success mt-20';
            resultBox.innerHTML = `
                <p><strong>✓ Institution Registered Successfully!</strong></p>
                <p><strong>ID:</strong> ${data.id} | <strong>Name:</strong> ${data.name}</p>
                <p><strong>Email:</strong> ${data.email} | <strong>Address:</strong> ${data.address || 'N/A'}</p>
            `;
            resultBox.classList.remove('hidden');
            document.getElementById('institutionForm').reset();
            loadInstitutions();
        } else {
            showToast(data.error || 'Failed to register institution', 'error');
        }
    } catch (err) {
        showToast('Network error while registering institution', 'error');
        console.error(err);
    }
}

async function loadInstitutions() {
    try {
        const response = await fetch('/api/institutions');
        if (!response.ok) throw new Error('Failed to fetch institutions');
        const institutions = await response.json();

        // Render Table
        const tbody = document.getElementById('institutionTableBody');
        if (institutions.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center">No institutions registered yet.</td></tr>';
        } else {
            tbody.innerHTML = institutions.map(i => `
                <tr>
                    <td><strong>${i.id}</strong></td>
                    <td>${i.name}</td>
                    <td>${i.email}</td>
                    <td>${i.address || 'N/A'}</td>
                </tr>
            `).join('');
        }

        // Populate Select Dropdowns for Certificates & Invoices
        populateInstSelect('issueInstId', institutions);
        populateInstSelect('invInstId', institutions);

    } catch (err) {
        console.error('Error loading institutions:', err);
    }
}

function populateInstSelect(elementId, institutions) {
    const select = document.getElementById(elementId);
    if (!select) return;
    const currentVal = select.value;
    select.innerHTML = '<option value="">Select Institution</option>' +
        institutions.map(i => `<option value="${i.id}">${i.name} (ID: ${i.id})</option>`).join('');
    select.value = currentVal;
}

// -------------------------------------------------------------
// CERTIFICATE ISSUANCE & VERIFICATION
// -------------------------------------------------------------
async function issueCertificate(event) {
    event.preventDefault();
    const studentName = document.getElementById('studentName').value.trim();
    const course = document.getElementById('course').value.trim();
    const institutionId = document.getElementById('issueInstId').value;

    const placeholder = document.getElementById('issueResultPlaceholder');
    const resultCard = document.getElementById('issueResult');

    try {
        const response = await fetch('/api/certificates', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ studentName, course, institutionId: Number(institutionId) })
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Certificate issued successfully!');
            placeholder.classList.add('hidden');

            document.getElementById('resCertId').textContent = data.certificateId;
            document.getElementById('resStudent').textContent = data.studentName;
            document.getElementById('resCourse').textContent = data.course;
            document.getElementById('resIssuedDate').textContent = data.issuedDate;
            document.getElementById('resHash').textContent = data.hash;

            resultCard.classList.remove('hidden');
            document.getElementById('issueForm').reset();
        } else {
            showToast(data.error || 'Failed to issue certificate', 'error');
        }
    } catch (err) {
        showToast('Network error while issuing certificate', 'error');
        console.error(err);
    }
}

function copyHash() {
    const hashText = document.getElementById('resHash').textContent;
    if (hashText) {
        navigator.clipboard.writeText(hashText);
        showToast('Hash copied to clipboard!');
    }
}

async function verifyCertificate(event) {
    event.preventDefault();
    const hash = document.getElementById('verifyHash').value.trim();
    const resultBox = document.getElementById('verifyResult');

    if (!hash) {
        showToast('Please enter a certificate hash to verify', 'error');
        return;
    }

    try {
        const response = await fetch(`/api/certificates/verify/${encodeURIComponent(hash)}`);
        const data = await response.json();

        resultBox.classList.remove('hidden');

        if (response.ok && data.valid) {
            resultBox.className = 'verify-result-box verify-success';
            resultBox.innerHTML = `
                <h4>✓ Certificate Verified</h4>
                <div class="cert-body">
                    <div class="detail-row"><span class="label">Certificate ID:</span><span class="value font-bold">${data.certificateId}</span></div>
                    <div class="detail-row"><span class="label">Student Name:</span><span class="value">${data.studentName}</span></div>
                    <div class="detail-row"><span class="label">Course:</span><span class="value">${data.course}</span></div>
                    <div class="detail-row"><span class="label">Institution:</span><span class="value">${data.institution}</span></div>
                    <div class="detail-row"><span class="label">Issued Date:</span><span class="value">${data.issuedDate}</span></div>
                    <div class="hash-box" style="margin-top:10px;">
                        <span class="label">Verified SHA-256 Hash:</span>
                        <code style="word-break:break-all;">${data.hash}</code>
                    </div>
                </div>
            `;
        } else {
            resultBox.className = 'verify-result-box verify-failed';
            resultBox.innerHTML = `
                <h4>✗ Invalid Certificate</h4>
                <p>${data.message || 'Certificate is invalid or not found.'}</p>
            `;
        }
    } catch (err) {
        resultBox.classList.remove('hidden');
        resultBox.className = 'verify-result-box verify-failed';
        resultBox.innerHTML = `
            <h4>✗ Error</h4>
            <p>Network error while verifying certificate.</p>
        `;
    }
}

// -------------------------------------------------------------
// BILLING & PAYMENTS
// -------------------------------------------------------------
async function createInvoice(event) {
    event.preventDefault();
    const institutionId = document.getElementById('invInstId').value;
    const packageName = document.getElementById('packageName').value.trim();
    const amount = document.getElementById('amount').value;
    const resultBox = document.getElementById('invoiceResult');

    try {
        const response = await fetch('/api/invoices', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ institutionId: Number(institutionId), packageName, amount: Number(amount) })
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Invoice created successfully!');
            resultBox.className = 'result-box alert-success mt-20';
            resultBox.innerHTML = `
                <p><strong>✓ Invoice Generated!</strong></p>
                <p><strong>Invoice ID:</strong> ${data.id} | <strong>Package:</strong> ${data.packageName}</p>
                <p><strong>Amount:</strong> $${data.amount} | <strong>Status:</strong> <span class="badge badge-warning">${data.status}</span></p>
            `;
            resultBox.classList.remove('hidden');
            document.getElementById('invoiceForm').reset();
            loadInvoices();
        } else {
            showToast(data.error || 'Failed to create invoice', 'error');
        }
    } catch (err) {
        showToast('Network error while creating invoice', 'error');
    }
}

async function loadInvoices() {
    try {
        const response = await fetch('/api/invoices');
        if (!response.ok) throw new Error('Failed to fetch invoices');
        const invoices = await response.json();

        const tbody = document.getElementById('invoiceTableBody');
        const paySelect = document.getElementById('payInvoiceId');

        if (invoices.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center">No invoices generated yet.</td></tr>';
            paySelect.innerHTML = '<option value="">No Pending Invoices</option>';
        } else {
            tbody.innerHTML = invoices.map(inv => `
                <tr>
                    <td><strong>#${inv.id}</strong></td>
                    <td>Inst #${inv.institutionId}</td>
                    <td>${inv.packageName}</td>
                    <td>$${inv.amount ? inv.amount.toFixed(2) : '0.00'}</td>
                    <td>${inv.invoiceDate || 'N/A'}</td>
                    <td><span class="badge ${inv.status === 'PAID' ? 'badge-success' : 'badge-warning'}">${inv.status}</span></td>
                </tr>
            `).join('');

            // Populate Pending Invoices in Payment Form
            const pendingInvoices = invoices.filter(inv => inv.status === 'PENDING');
            if (pendingInvoices.length === 0) {
                paySelect.innerHTML = '<option value="">No Pending Invoices</option>';
            } else {
                paySelect.innerHTML = '<option value="">Select Pending Invoice</option>' +
                    pendingInvoices.map(inv => `<option value="${inv.id}">Invoice #${inv.id} - ${inv.packageName} ($${inv.amount})</option>`).join('');
            }
        }
    } catch (err) {
        console.error('Error loading invoices:', err);
    }
}

async function makePayment(event) {
    event.preventDefault();
    const invoiceId = document.getElementById('payInvoiceId').value;
    const paymentMethod = document.getElementById('paymentMethod').value;
    const resultBox = document.getElementById('paymentResult');

    if (!invoiceId) {
        showToast('Please select a valid pending invoice', 'error');
        return;
    }

    try {
        const response = await fetch('/api/payments', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ invoiceId: Number(invoiceId), paymentMethod })
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Payment successful!');
            resultBox.className = 'result-box alert-success mt-20';
            resultBox.innerHTML = `
                <p><strong>✓ Payment Successful!</strong></p>
                <p><strong>Payment ID:</strong> ${data.id} | <strong>Invoice ID:</strong> #${data.invoiceId}</p>
                <p><strong>Amount Paid:</strong> $${data.amount} | <strong>Method:</strong> ${data.paymentMethod}</p>
                <p><strong>Invoice Status:</strong> <span class="badge badge-success">PAID</span></p>
            `;
            resultBox.classList.remove('hidden');
            loadInvoices();
            loadReports();
        } else {
            showToast(data.error || 'Payment failed', 'error');
        }
    } catch (err) {
        showToast('Network error processing payment', 'error');
    }
}

// -------------------------------------------------------------
// REPORTS & ANALYTICS
// -------------------------------------------------------------
async function loadReports() {
    try {
        // Fetch Platform P&L
        const plRes = await fetch('/api/reports/pl');
        if (plRes.ok) {
            const plData = await plRes.json();
            document.getElementById('plRevenue').textContent = `$${(plData.totalRevenue || 0).toFixed(2)}`;
            document.getElementById('plPayments').textContent = `$${(plData.totalPayments || 0).toFixed(2)}`;
            document.getElementById('plProfit').textContent = `$${(plData.profit || 0).toFixed(2)}`;
        }

        // Fetch Unit Budget Report
        const budRes = await fetch('/api/reports/budget');
        if (budRes.ok) {
            const budData = await budRes.json();
            document.getElementById('budCert').textContent = `$${(budData.certificateVerification || 0).toFixed(2)}`;
            document.getElementById('budApi').textContent = `$${(budData.apiBilling || 0).toFixed(2)}`;
            document.getElementById('budTotal').textContent = `$${(budData.total || 0).toFixed(2)}`;
        }
    } catch (err) {
        console.error('Error loading reports:', err);
    }
}
