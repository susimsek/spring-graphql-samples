import {NextRequest, NextResponse} from "next/server";

export function middleware(request: NextRequest){
    const cookie = request.cookies.get("token")?.value
    console.log('cookie -> ' + cookie)
    if (cookie === undefined) {
        return NextResponse.redirect(new URL('/login', request.url))
    }
    return NextResponse.next()
}

export const config = {
    matcher: [
        /*
         * Match all request paths except for the ones starting with:
         * - api (API routes)
         * - _next/static (static files)
         * - favicon.ico (favicon file)
         */
        '/((?!api|_next/static|favicon.ico|login).*)',
    ],
}