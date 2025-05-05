import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {EuroCrateDto} from '../api/models/euro-crate-dto';
import {DeliveryStateEnumDto, LocationTypeDto, LogisticsLocationDto, OperationCenterDto} from '../api/models';

function stringify(p: EuroCrateDto) {
	return `${(p.internalId+"").padStart(7, '0')}  ${p.operationCenter.substring(0, 10).padStart(10, " ")} ${p.name.substring(0, 10).padEnd(10, " ")}  ${p.deliveryState}`
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
	ngAfterViewInit() {
		this.canv = this.canvR.nativeElement;
		this.canv.width = window.innerWidth;
		this.canv.height = window.innerHeight;
		console.log(this.canv)
		this.ctx = this.canv.getContext("2d")!
		this.setup()
		requestAnimationFrame(this.drawFrame.bind(this))
		setInterval(() => {
			let someThings = Array(this.rows).fill(" ").map(_ => {
				let v = Object.values(DeliveryStateEnumDto)
				let v1 = Object.values(OperationCenterDto)
				return {
					deliveryState: v[Math.floor(Math.random() * v.length)],
					internalId: Math.floor(Math.random() * 1000),
					location: { logisticsLocation: LogisticsLocationDto.Loc,locationType: LocationTypeDto.Logistics },
					operationCenter: v1[Math.floor(Math.random() * v1.length)],
					name: "Huh?",
					returnBy: "ee"
				} as EuroCrateDto
			})
			let c = someThings.map(p => stringify(p)).join("\n")
			console.log(c)
			this.setText(c)
		}, 5000)
	}

	aLetterWidth: number = 0
	aLetterHeight: number = 0
	rows = 0
	cols = 0
	haveMatrix: Array<number> = []
	wantMatrix: Array<number> = []
	canUpdateAt: Array<number> = []
	lastSeenOk: Array<number> = []

	glyphs = " !\"#$&'()*+€,-./\\^:;<=>?0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	dirty = false

	setText(s: string) {
		s = s.toUpperCase()
		let lines = s.split("\n");
		for (let i = 0; i < lines.length; i++) {
			let line = lines[i]
			for(let j = 0; j < this.cols; j++) {
				let idx = this.glyphs.indexOf(j < line.length ? line[j] : " ")
				if (idx < 0) idx = 0;
				this.wantMatrix[i * this.cols + j] = idx;
			}
		}
		this.dirty = true
	}

	setup() {
		this.ctx.clearRect(0, 0, this.canv.width, this.canv.height)
		this.ctx.font = "41px Skyfont"
		let measureText = this.ctx.measureText("A")
		this.aLetterWidth = measureText.width
		this.aLetterHeight = 30
		let cols = this.cols = Math.floor(this.canv.width / measureText.width)
		let rows = this.rows = Math.floor(this.canv.height / this.aLetterHeight)

		this.haveMatrix = Array(rows * cols).fill(1)
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
			this.ctx.font = "41px Skyfont"
			let any = false
			if (this.dirty) {
				for (let i = 0; i < this.haveMatrix.length; i++) {
					if (this.haveMatrix[i] != this.wantMatrix[i]) {
						any = true;
						if (time - this.lastSeenOk[i] < (Math.random() * 1500 + 300)) continue // "spin up" time
						if (time < this.canUpdateAt[i]) continue // wait to update another time
						this.canUpdateAt[i] = time + (Math.random() * 40)
						let direction = Math.sign(this.wantMatrix[i] - this.haveMatrix[i])
						let thisOneX = i % this.cols * this.aLetterWidth;
						let thisOneY = (Math.floor(i / this.cols)) * this.aLetterHeight;
						this.ctx.clearRect(thisOneX, thisOneY, this.aLetterWidth, this.aLetterHeight);
						this.ctx.fillStyle = "#FFF"
						this.ctx.fillRect(thisOneX + 2, thisOneY + 2, this.aLetterWidth - 4, this.aLetterHeight - 4);
						this.ctx.fillStyle = "#333"
						this.haveMatrix[i] += direction;
						this.ctx.fillText(this.glyphs[this.haveMatrix[i]], thisOneX, thisOneY + this.aLetterHeight)
						// break;
					} else {
						this.lastSeenOk[i] = time
					}
				}
				if (!any) this.dirty = false
			} else {
				this.lastSeenOk = this.lastSeenOk.fill(time)
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
