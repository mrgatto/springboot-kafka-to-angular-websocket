import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from '@stomp/ng2-stompjs';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MessageStreamComponent } from './message-stream/message-stream.component';
import { myRxStompConfig } from './rx-stomp.config';

@NgModule({
   declarations: [
      AppComponent,
      MessageStreamComponent
   ],
   imports: [
      BrowserModule,
      HttpClientModule,
      FormsModule,
      ReactiveFormsModule,
      AppRoutingModule
   ],
   providers: [
      {
         provide: InjectableRxStompConfig,
         useValue: myRxStompConfig
      },
      {
         provide: RxStompService,
         useFactory: rxStompServiceFactory,
         deps: [InjectableRxStompConfig]
      }
   ],
   bootstrap: [
      AppComponent
   ]
})
export class AppModule { }
