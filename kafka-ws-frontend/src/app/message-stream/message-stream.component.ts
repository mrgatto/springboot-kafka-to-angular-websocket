import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
import { Observable, of, Subject } from 'rxjs';
import { catchError, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-message-stream',
  templateUrl: './message-stream.component.html',
  styleUrls: ['./message-stream.component.css']
})
export class MessageStreamComponent implements OnInit, OnDestroy {

  myForm: FormGroup;

  messages: string[];

  private destroy$ = new Subject();

  constructor(private frmBuilder: FormBuilder,
    private http: HttpClient,
    private rxStompService: RxStompService) {

    this.myForm = frmBuilder.group(
      { nMessage: '10' }
    );
  }

  ngOnInit(): void {
    this.messages = [];

    this.rxStompService.watch('/topic/messages')
      .pipe(
        takeUntil(this.destroy$)
      ).subscribe((message: Message) => {
        console.log('Received from websocket: ' + message.body);
        this.messages.push(message.body);
        this.messages = this.messages.slice(-5);
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next(null);
    this.destroy$.unsubscribe();
  }

  submit(): void {
    const nMessage = this.myForm.controls.nMessage.value;

    this.http.get(`/api/kafka/sample/${nMessage}`, { observe: 'response' })
      .pipe(
        catchError(this.handleError.bind(this)),
        takeUntil(this.destroy$)
      ).subscribe((resp: HttpResponse<any>) => {

      });
  }

  private handleError(error: HttpErrorResponse): Observable<any> {
    return of(null);
  }

}
