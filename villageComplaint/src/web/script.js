const complaintForm = document.getElementById('complaintForm');

if (complaintForm) {

    complaintForm.addEventListener('submit', async function (e) {

        e.preventDefault();

        const body = new URLSearchParams(new FormData(this));

        console.log("Sending complaint data...");

        try {

            const response = await fetch('/addComplaint', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: body
            });

            const result = await response.text();

            document.getElementById('message').textContent = result;

            if (response.ok) {

                alert("Complaint Submitted Successfully");

                this.reset();

                getComplaint();
            }

        } catch (error) {

            console.error("Error submitting complaint:", error);

            document.getElementById('message').textContent =
                "Error submitting complaint";
        }

    });

}


async function getComplaint() {

    try {

        const response = await fetch('/complaints');

        console.log("Status:", response.status);

        if (!response.ok) {
            throw new Error("Failed to load complaints");
        }

        const data = await response.json();

        console.log("Database data:", data);

        const total = data.length;

        const pending = data.filter(
            complaint => complaint.status === 'Pending'
        ).length;

        const progress = data.filter(
            complaint => complaint.status === 'Progress'
        ).length;

        const resolved = data.filter(
            complaint => complaint.status === 'Resolved'
        ).length;


        const totalCount = document.getElementById('total-count');
        const pendingCount = document.getElementById('pending-count');
        const progressCount = document.getElementById('progress-count');
        const resolvedCount = document.getElementById('resolved-count');


        if (totalCount)
            totalCount.textContent = total;

        if (pendingCount)
            pendingCount.textContent = pending;

        if (progressCount)
            progressCount.textContent = progress;

        if (resolvedCount)
            resolvedCount.textContent = resolved;

        const table = document.getElementById('complaintTable');

        if (!table) {
            return;
        }


        table.innerHTML = data.map(s => `

            <tr>

                <td>${s.id}</td>

                <td>${s.name}</td>

                <td>${s.phone}</td>

                <td>${s.village_name}</td>

                <td>${s.pincode}</td>

                <td>${s.category}</td>

                <td>${s.description}</td>

                <td>${s.location}</td>


                <!-- STATUS / ACTION -->

                <td>

                    <select
                        onchange="updateComplaint(${s.id}, this.value)"
                    >

                        <option
                            value="Pending"
                            ${s.status === "Pending" ? "selected" : ""}
                        >
                            Pending
                        </option>


                        <option
                            value="Progress"
                            ${s.status === "Progress" ? "selected" : ""}
                        >
                            Progress
                        </option>


                        <option
                            value="Resolved"
                            ${s.status === "Resolved" ? "selected" : ""}
                        >
                            Resolved
                        </option>

                    </select>

                </td>


                <td>

                    ${s.status}

                </td>

            </tr>

        `).join('');


    } catch (error) {

        console.error("Error loading complaints:", error);

    }

}


async function updateComplaint(id, status) {

    console.log("Complaint ID:", id);

    console.log("Selected Status:", status);


    try {

        const response = await fetch("/updateComplaint", {

            method: "POST",

            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },

            body: new URLSearchParams({

                id: id,

                status: status

            })

        });


        const result = await response.text();

        console.log("Server response:", result);


        if (response.ok) {

            alert("Complaint status updated successfully!");

            // Reload table and statistics
            getComplaint();

        } else {

            alert("Update failed: " + result);

        }


    } catch (error) {

        console.error("Update error:", error);

        alert("Error updating complaint");

    }

}


getComplaint();