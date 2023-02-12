import React from "react";
import {Direction, IMessage} from "../types/chat";
import {Spinner} from "react-bootstrap";

interface MessageProps {

    message?: IMessage | undefined;
    icon: React.ReactNode;

    spinnerText?: string;

    loading?: boolean;

    multiRowEnabled?: boolean;

    messages?: IMessage[];

    direction: Direction;

}

const Message: React.FC<MessageProps> = ({
                                             message,
                                             icon,
                                             direction,
                                             spinnerText,
                                             loading,
                                             multiRowEnabled,
                                             messages}) => {
    return (
      direction === Direction.RIGHT ? <div className="d-flex flex-row justify-content-end mb-4 pt-1">
          <div>
              <p className="small p-2 me-3 mb-1 text-white rounded-3 bg-primary">
                  {message?.message}
              </p>
          </div>
          {icon}
      </div>:
          <div className="d-flex flex-row justify-content-start">
              {icon}
              <div>
                  {multiRowEnabled ? messages?.map((message: IMessage) => (
                      <p
                          key={message.id}
                          className="small p-2 ms-3 mb-1 rounded-3"
                          style={{ backgroundColor: "#f5f6f7" }}
                      >
                          {loading ? <span><Spinner size="sm"
                                                                  variant="secondary"
                                                                  className="me-1"
                                                                  animation="grow"/>{spinnerText}</span> : message.message}
                      </p>
                  )): <div
                      className="small p-2 ms-3 mb-1 rounded-3"
                      style={{ backgroundColor: "#f5f6f7" }}
                  >
                      {loading ? <span><Spinner size="sm"
                                                                                         variant="secondary"
                                                                                         className="me-1"
                                                                                         animation="grow"/>{spinnerText}</span> : message?.message}
                  </div>}
              </div>
          </div>
    );
}

export default Message;