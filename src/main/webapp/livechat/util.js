function getTime(gInfo){
	const d = new Date();
	
	let hour = d.getHours();
	let minutes = d.getMinutes();
	let seconds = d.getSeconds();
	let mid = hour < 12 ? "오전" : "오후";
	hour = hour < 12 ? hour : hour-12;
	minutes = minutes < 10 ? '0'+minutes : minutes;
	seconds = seconds < 10 ? '0'+seconds : seconds;
	
	gInfo.innerHTML = `${mid} ${hour} : ${minutes} : ${seconds}`;
}
function modifyVote(id, totals){
	let status = totals.split(",");

	const voteDiv = document.getElementById("vote_" + id);
	const buttons = voteDiv.querySelectorAll("button");

	  const children = Array.from(voteDiv.childNodes);
	  children.forEach(node => {
	    if (node.nodeType === Node.TEXT_NODE) {
	      voteDiv.removeChild(node);
	    }
	  });

	  buttons.forEach((btn, i) => {
	    
	    voteDiv.insertBefore(document.createTextNode(` - ${status[i]}`), btn.nextSibling);
	    if (i < buttons.length - 1) {
	      voteDiv.insertBefore(document.createTextNode(" | "), btn.nextSibling.nextSibling);
	    }
		
		let onclick = btn.getAttribute("onclick");

		    if (onclick) {
		     	
		      let newOnclick = onclick.replace(/,\s*'[^']*'\s*\)\s*$/, `, '${totals}')`);
		      btn.setAttribute("onclick", newOnclick);
		    }
	  });
}

function removeVote(idx){
	console.log(typeof idx);
	
	$.ajax({
		  url: 'http://localhost:8888/ChatApp/chat?type=remove_vote',
		  type: 'POST',
		  contentType: 'application/json',
		  data: JSON.stringify({content:"", date_info:"", id : idx, vote_total : ""}),
		  success: function (res) {
		    console.log('Saved to server:', res);
			if(!alert("투표가 삭제되었습니다."))
				location.reload();
		  },
		  error: function (xhr, status, err) {
		    console.error('Error:', err);
		  }
    });
	
}

function addVote(content, d, totals, id){
	let items = content.split(",");
	let status = totals.split(",");

	
	let date = d;
	let ele = `<p class='vote_title'>투표 - ${date} <button onclick="removeVote(${id})">X</button></p><div class='vote_content' id='vote_${id}'>`;
	
	for(let i in items){
		ele += `<button onclick="doVote(this,'${date}', '${id}', '${items}', '${status}' )" 
		value='${items[i]}'>${items[i]}</button> 
		- ${status[i]}`;
		if(i < items.length-1)
			ele+=" | ";
	}
	ele +="</div>"
	$('.vote').append(ele);
}

function doVote(button, date, id, items, status){
	items = items.split(",");
	status = status.split(",");
	cookieName = "vote_"+date;
	let isVoted = getCookie(cookieName);
	
	if(isVoted == null){
		setCookie(cookieName, "voted", 400);
		let totals = "";
		for(let i in items){
			
			if(items[i] == button.value){
				status[i] = parseInt(status[i])+1;
				
			}
			totals+=status[i];
			if(i < items.length -1)
				totals+=",";
		}
		console.log(totals);
		
		
		$.ajax({
          url: 'http://localhost:8888/ChatApp/chat?type=add_vote',
          type: 'POST',
          contentType: 'application/json',
          data: JSON.stringify({content:"", date_info:"", id : id, vote_total : totals}),
          success: function (res) {
            console.log('Saved to server:', res);
          },
          error: function (xhr, status, err) {
            console.error('Error:', err);
          }
        });
		alert("투표가 완료되었습니다.");
	}
	else
		alert("이미 투표 하셨습니다.");
}


function getTimeStamp(d){
	let mm = d.getMonth() < 10 ? '0'+(d.getMonth()+1) : (d.getMonth()+1);
	let dd = d.getDate() < 10 ? '0'+d.getDate() : d.getDate();
	let h = d.getHours() < 10 ? '0'+d.getHours() : d.getHours();
	let m = d.getMinutes() < 10 ? '0'+d.getMinutes() : d.getMinutes();
	let s = d.getSeconds() < 10 ? '0'+d.getSeconds() : d.getSeconds();
	
	return mm+"/"+dd+" "+h+":"+m+":"+s;
}


function addChatFromOthers(content, d, isVote){
	const chatSection = document.querySelector('.chat');
	const newChat = document.createElement('div');
	newChat.textContent = content;
	newChat.className = 'other_chat';


	const dateContent = document.createElement('div');
	dateContent.textContent = d;
	dateContent.className = 'other_chat_date';
	
	chatSection.appendChild(newChat);
	chatSection.appendChild(dateContent);

	chatSection.scrollTop = chatSection.scrollHeight;
}


function addChatFromMe(content, d, isVote){
	const chatSection = document.querySelector('.chat');
	
	
	const newChat = document.createElement('div');
	newChat.textContent = content;
	newChat.className = 'my_chat';
	chatSection.appendChild(newChat);

	

	const dateContent = document.createElement('div');
	dateContent.textContent = d;
	dateContent.className = 'my_chat_date';
	
	
	chatSection.appendChild(dateContent);
	
		

	chatSection.scrollTop = chatSection.scrollHeight;
}