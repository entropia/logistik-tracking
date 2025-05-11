import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {EuroCrateDto} from '../api/models/euro-crate-dto';
import {DeliveryStateEnumDto} from '../api/models';
import {ApiService} from '../api/services/api.service';
import {HttpErrorResponse} from '@angular/common/http';

const ocMap = {
	Finanzen: 'FINAZ',
	Backoffice: 'BACKO',
	Content: 'CNTNT',
	Heralding: 'HRALD',
	DesignUndMotto: 'DESNG',
	PresseUndSocialMedia: 'PRESS',
	LoungeControl: 'LNGE',
	LoungeTechnik: 'LNGET',
	Infodesk: 'INFO',
	Merchdesk: 'MERCH',
	Schilder: 'SCHLD',
	Badges: 'BADGE',
	Trolle: 'TROLL',
	Kueche: 'KUECH',
	WOC: 'WOC',
	Fruehstueck: 'FRUEH',
	RaumDer1000Namen: 'RD1000',
	Bar: 'BAR',
	Spaeti: 'SPAET',
	Aussenbar: 'AUBAR',
	Kaffeebar: 'KFBAR',
	Cocktailbar: 'CTBAR',
	NOC: 'NOC',
	POC: 'POC',
	VOC: 'VOC',
	BuildupAndTeardown: 'BUILD',
	Infrastruktur: 'INFRA',
	Deko: 'DEKO',
	SafeR: 'SAFER',
	SilentHacking: 'SLNTH',
	Projektleitung: 'PL'
}

const statusMap = {
	Packing: 'PACKING',
	WaitingForDelivery: 'WAITDELIVERY',
	TravelingToGPN: 'TRANSPORT',
	WaitingAtGPN: 'WAITGPN FINGER WEG',
	InDelivery: 'DELIVERY',
	Delivered: 'DELIVERED'

}

function stringify(p: EuroCrateDto) {
	return `${(p.internalId+"").padStart(7, '0')}  ${ocMap[p.operationCenter].padStart(5, " ")}  ${p.name.substring(0, 10).padEnd(10, " ")}  ${statusMap[p.deliveryState]}`
}

@Component({
  selector: 'app-departures',
  imports: [],
  templateUrl: './departures.component.html',
  styleUrl: './departures.component.scss'
})
export class DeparturesComponent implements AfterViewInit{
	@ViewChild("theCanvas") canvR!: ElementRef<HTMLCanvasElement>;
	canv!: HTMLCanvasElement;
	ctx!: CanvasRenderingContext2D;
	@ViewChild("page") page!: ElementRef<HTMLImageElement>;

	constructor(
		private api: ApiService
	) {
	}

	updateData() {
		this.api.getAllEuroCrates()
			.subscribe({
				next: v => {
					this.setText(v.filter(it => it.deliveryState != DeliveryStateEnumDto.Delivered).map(e => stringify(e)).join("\n"))
				},
				error: e => {
					console.error(e)
					let msg: string;
					if (e instanceof HttpErrorResponse) {
						msg = `Daten nicht abrufbar  server antwortete mit code ${e.status}\n${JSON.stringify(e.error, undefined, 2)}`
					} else if (e instanceof Error) {
						msg = `Daten nicht abrufbar\n${e.message}`
					} else {
						msg = "Daten nicht abrufbar  unbekannter fehler"
					}
					this.setText(msg)
				}
			})
	}

	ngAfterViewInit() {
		this.canv = this.canvR.nativeElement;
		this.canv.width = window.innerWidth;
		this.canv.height = window.innerHeight;
		console.log(this.canv)
		this.ctx = this.canv.getContext("2d")!
		this.setup()
		// FIXME wtf
		setTimeout(() => {
			requestAnimationFrame(this.drawFrame.bind(this))
		}, 2000)
		this.updateData()
		setInterval(() => {
			this.updateData()
		}, 6000)
	}

	aLetterWidth: number = 0
	aLetterHeight: number = 0
	margin = 2
	rows = 0
	cols = 0
	haveMatrix: Array<number> = []
	wantMatrix: Array<number> = []
	canUpdateAt: Array<number> = []
	lastSeenOk: Array<number> = []

	glyphs = " ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?"
	dirty = false

	g_columns = 5
	g_rows = this.glyphs.length
	g_width = 60
	g_height = 80

	setText(s: string) {
		s = s.toUpperCase()
		let lines = s.split("\n");
		this.wantMatrix.fill(0)
		for (let i = 0; i < lines.length; i++) {
			let line = lines[i]
			for(let j = 0; j < this.cols; j++) {
				let idx = this.glyphs.indexOf(j < line.length ? line[j] : " ")
				if (idx < 0) idx = 0;
				this.wantMatrix[i * this.cols + j] = idx * this.g_columns;
			}
		}
		this.dirty = true
	}

	setup() {
		// this.ctx.clearRect(0, 0, this.canv.width, this.canv.height)
		// this.ctx.font = "41px Skyfont"
		// let measureText = this.ctx.measureText("A")
		this.aLetterWidth = this.g_width / 2
		this.aLetterHeight = this.g_height / 2
		let cols = this.cols = Math.floor(this.canv.width / (this.aLetterWidth + this.margin))
		let rows = this.rows = Math.floor(this.canv.height / (this.aLetterHeight + this.margin))

		this.haveMatrix = Array(rows * cols).fill(-1)
		this.wantMatrix = Array(rows * cols).fill(0)
		this.canUpdateAt = Array(rows * cols).fill(0)
		this.lastSeenOk = Array(rows * cols).fill(0)
		this.dirty = true

		// this.ctx.fillStyle = "#333"
		// for(let i = 0; i < rows; i++) {
		// 	this.ctx.fillText(" ".repeat(cols), 0, (i+1) * this.aLetterHeight)
		// }
	}

	lastUpdateTime = 0

	drawFrame(time: number) {

		if (time - this.lastUpdateTime > (1 / 60 * 1000)) {
			this.lastUpdateTime = time
			// this.ctx.font = "41px Skyfont"
			let any = false
			if (this.dirty) {
				for (let i = 0; i < this.haveMatrix.length; i++) {
					if (this.haveMatrix[i] != this.wantMatrix[i]) {
						any = true;
						if (time - this.lastSeenOk[i] < (Math.random() * 300 + 300)) continue // "spin up" time
						if (time < this.canUpdateAt[i]) continue // wait to update another time
						this.canUpdateAt[i] = time + (Math.random() * (40/5))
						let thisOneX = i % this.cols * (this.aLetterWidth + this.margin);
						let thisOneY = (Math.floor(i / this.cols)) * (this.aLetterHeight + this.margin);
						// this.ctx.clearRect(thisOneX, thisOneY, this.aLetterWidth, this.aLetterHeight);
						// this.ctx.fillStyle = "#FFF"
						// this.ctx.fillRect(thisOneX + 2, thisOneY + 2, this.aLetterWidth - 4, this.aLetterHeight - 4);
						// this.ctx.fillStyle = "#333"
						this.haveMatrix[i] = (this.haveMatrix[i] + 1) % (this.g_rows * this.g_columns);
						let index = this.haveMatrix[i]
						let theRow = Math.floor(index / this.g_columns)
						let theColumn = index % this.g_columns
						this.ctx.drawImage(this.page.nativeElement, theColumn * this.g_width, theRow * this.g_height, this.g_width, this.g_height, thisOneX, thisOneY, this.aLetterWidth, this.aLetterHeight)
						// this.ctx.fillText(this.glyphs[this.haveMatrix[i]], thisOneX, thisOneY + this.aLetterHeight)
						// break;
					} else {
						this.lastSeenOk[i] = time
					}
				}
				if (!any) this.dirty = false
			} else {
				this.lastSeenOk.fill(time)
			}
		}
		// this.ctx.clearRect(0, 0, this.canv.width, this.canv.height)
		// this.ctx.font = "40px Skyfont"
		// this.ctx.fillStyle = "#000"
		// this.ctx.fillText("Good morning usa", (time % 5000) / 5000 * 200, 40)
		requestAnimationFrame((time) => {
			this.drawFrame(time)
		})
	}
}
